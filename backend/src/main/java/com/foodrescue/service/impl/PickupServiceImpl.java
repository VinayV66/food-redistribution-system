package com.foodrescue.service.impl;

import com.foodrescue.dto.response.PagedResponse;
import com.foodrescue.dto.response.PickupResponse;
import com.foodrescue.entity.FoodDonation;
import com.foodrescue.entity.Pickup;
import com.foodrescue.entity.User;
import com.foodrescue.enums.DonationStatus;
import com.foodrescue.enums.NotificationType;
import com.foodrescue.enums.PickupStatus;
import com.foodrescue.exception.*;
import com.foodrescue.repository.FoodDonationRepository;
import com.foodrescue.repository.PickupRepository;
import com.foodrescue.repository.UserRepository;
import com.foodrescue.service.AuditLogService;
import com.foodrescue.service.NotificationService;
import com.foodrescue.service.PickupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PickupServiceImpl implements PickupService {

    private final PickupRepository pickupRepository;
    private final FoodDonationRepository donationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    @CacheEvict(value = "availableDonations", allEntries = true)
    public PickupResponse createPickup(Long donationId, Long ngoId) {
        FoodDonation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new ResourceNotFoundException("FoodDonation", donationId));

        if (donation.getStatus() == DonationStatus.EXPIRED) {
            throw new DonationExpiredException("This donation has expired and cannot be accepted.");
        }
        if (donation.getStatus() != DonationStatus.AVAILABLE &&
            donation.getStatus() != DonationStatus.REQUESTED) {
            throw new BadRequestException("Donation is not available for pickup. Current status: " + donation.getStatus());
        }
        if (pickupRepository.findByDonationId(donationId).isPresent()) {
            throw new BadRequestException("A pickup already exists for this donation.");
        }

        User ngo = userRepository.findById(ngoId)
                .orElseThrow(() -> new ResourceNotFoundException("User", ngoId));

        // Update donation status and assign NGO
        donation.setStatus(DonationStatus.ACCEPTED);
        donation.setAssignedNgo(ngo);
        donationRepository.save(donation);

        Pickup pickup = Pickup.builder()
                .donation(donation)
                .ngo(ngo)
                .pickupAddress(donation.getPickupAddress())
                .scheduledPickupTime(donation.getPickupStartTime())
                .status(PickupStatus.PENDING)
                .build();
        pickup = pickupRepository.save(pickup);

        // Notify donor
        notificationService.sendNotification(
                donation.getDonor().getId(),
                "Donation Accepted!",
                "Your donation '" + donation.getFoodName() + "' has been accepted by an NGO.",
                NotificationType.DONATION_ACCEPTED);

        auditLogService.log(ngoId, "PICKUP_CREATED", "Pickup", pickup.getId(),
                "NGO accepted donation: " + donationId);

        return toResponse(pickup);
    }

    @Override
    @Transactional(readOnly = true)
    public PickupResponse getPickupById(Long id) {
        return toResponse(pickupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pickup", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PickupResponse> getPickupsForNgo(Long ngoId, Pageable pageable) {
        return PagedResponse.of(pickupRepository.findByNgoId(ngoId, pageable).map(this::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PickupResponse> getPickupsForVolunteer(Long volunteerId, Pageable pageable) {
        return PagedResponse.of(pickupRepository.findByVolunteerId(volunteerId, pageable).map(this::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PickupResponse> getAvailablePickups(Pageable pageable) {
        return PagedResponse.of(
                pickupRepository.findByVolunteerIsNullAndStatus(PickupStatus.PENDING, pageable)
                        .map(this::toResponse));
    }

    @Override
    @Transactional
    public PickupResponse assignVolunteer(Long pickupId, Long volunteerId) {
        Pickup pickup = getPickupOrThrow(pickupId);

        if (pickup.getStatus() != PickupStatus.PENDING) {
            throw new InvalidStatusTransitionException(pickup.getStatus().name(), "ASSIGNED");
        }

        User volunteer = userRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", volunteerId));

        pickup.setVolunteer(volunteer);
        pickup.setStatus(PickupStatus.ASSIGNED);
        pickup = pickupRepository.save(pickup);

        // Update donation status
        FoodDonation donation = pickup.getDonation();
        donation.setStatus(DonationStatus.PICKUP_ASSIGNED);
        donationRepository.save(donation);

        notificationService.sendNotification(volunteerId,
                "New Pickup Task!",
                "You have been assigned a pickup for '" + donation.getFoodName() + "'.",
                NotificationType.PICKUP_ASSIGNED);

        auditLogService.log(volunteerId, "VOLUNTEER_ASSIGNED", "Pickup", pickupId,
                "Volunteer " + volunteerId + " assigned to pickup " + pickupId);
        return toResponse(pickup);
    }

    @Override
    @Transactional
    public PickupResponse acceptPickup(Long pickupId, Long volunteerId) {
        Pickup pickup = getPickupOrThrow(pickupId);
        validateVolunteerOwnership(pickup, volunteerId);

        if (pickup.getStatus() != PickupStatus.ASSIGNED) {
            throw new InvalidStatusTransitionException(pickup.getStatus().name(), "ACCEPTED");
        }

        pickup.setStatus(PickupStatus.ACCEPTED);
        pickup = pickupRepository.save(pickup);
        auditLogService.log(volunteerId, "PICKUP_ACCEPTED", "Pickup", pickupId, "Volunteer accepted pickup");
        return toResponse(pickup);
    }

    @Override
    @Transactional
    public PickupResponse markCollected(Long pickupId, Long volunteerId) {
        Pickup pickup = getPickupOrThrow(pickupId);
        validateVolunteerOwnership(pickup, volunteerId);

        if (pickup.getStatus() != PickupStatus.ACCEPTED) {
            throw new InvalidStatusTransitionException(pickup.getStatus().name(), "COLLECTED");
        }

        pickup.setStatus(PickupStatus.COLLECTED);
        pickup.setCollectedAt(LocalDateTime.now());
        pickup = pickupRepository.save(pickup);

        FoodDonation donation = pickup.getDonation();
        donation.setStatus(DonationStatus.COLLECTED);
        donationRepository.save(donation);

        notificationService.sendNotification(donation.getDonor().getId(),
                "Food Collected!",
                "Your donation '" + donation.getFoodName() + "' has been collected by the volunteer.",
                NotificationType.FOOD_COLLECTED);

        auditLogService.log(volunteerId, "FOOD_COLLECTED", "Pickup", pickupId, "Food marked as collected");
        return toResponse(pickup);
    }

    @Override
    @Transactional
    public PickupResponse markDelivered(Long pickupId, Long volunteerId) {
        Pickup pickup = getPickupOrThrow(pickupId);
        validateVolunteerOwnership(pickup, volunteerId);

        if (pickup.getStatus() != PickupStatus.COLLECTED) {
            throw new InvalidStatusTransitionException(pickup.getStatus().name(), "DELIVERED");
        }

        pickup.setStatus(PickupStatus.DELIVERED);
        pickup.setDeliveredAt(LocalDateTime.now());
        pickup = pickupRepository.save(pickup);

        FoodDonation donation = pickup.getDonation();
        donation.setStatus(DonationStatus.COMPLETED);
        donationRepository.save(donation);

        notificationService.sendNotification(donation.getDonor().getId(),
                "Donation Completed!",
                "Your donation '" + donation.getFoodName() + "' has been delivered. Thank you for helping!",
                NotificationType.FOOD_DELIVERED);

        auditLogService.log(volunteerId, "FOOD_DELIVERED", "Pickup", pickupId, "Food marked as delivered");
        return toResponse(pickup);
    }

    @Override
    @Transactional
    public PickupResponse cancelPickup(Long pickupId, Long requestingUserId) {
        Pickup pickup = getPickupOrThrow(pickupId);

        if (pickup.getStatus() == PickupStatus.DELIVERED || pickup.getStatus() == PickupStatus.COLLECTED) {
            throw new BadRequestException("Cannot cancel a pickup that is already collected or delivered.");
        }

        pickup.setStatus(PickupStatus.CANCELLED);
        pickup = pickupRepository.save(pickup);

        // Revert donation to AVAILABLE so another NGO can pick it up
        FoodDonation donation = pickup.getDonation();
        donation.setStatus(DonationStatus.AVAILABLE);
        donation.setAssignedNgo(null);
        donationRepository.save(donation);

        auditLogService.log(requestingUserId, "PICKUP_CANCELLED", "Pickup", pickupId, "Pickup cancelled");
        return toResponse(pickup);
    }

    private Pickup getPickupOrThrow(Long id) {
        return pickupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pickup", id));
    }

    private void validateVolunteerOwnership(Pickup pickup, Long volunteerId) {
        if (pickup.getVolunteer() == null || !pickup.getVolunteer().getId().equals(volunteerId)) {
            throw new UnauthorizedException("You are not assigned to this pickup.");
        }
    }

    public PickupResponse toResponse(Pickup p) {
        return PickupResponse.builder()
                .id(p.getId())
                .donationId(p.getDonation().getId())
                .donationFoodName(p.getDonation().getFoodName())
                .ngoId(p.getNgo().getId())
                .ngoName(p.getNgo().getFullName())
                .volunteerId(p.getVolunteer() != null ? p.getVolunteer().getId() : null)
                .volunteerName(p.getVolunteer() != null ? p.getVolunteer().getFullName() : null)
                .pickupAddress(p.getPickupAddress())
                .deliveryAddress(p.getDeliveryAddress())
                .scheduledPickupTime(p.getScheduledPickupTime())
                .assignedAt(p.getAssignedAt())
                .collectedAt(p.getCollectedAt())
                .deliveredAt(p.getDeliveredAt())
                .status(p.getStatus())
                .notes(p.getNotes())
                .build();
    }
}
