package com.foodrescue.service.impl;

import com.foodrescue.dto.request.CreateDonationRequest;
import com.foodrescue.dto.response.DonationResponse;
import com.foodrescue.dto.response.PagedResponse;
import com.foodrescue.entity.FoodDonation;
import com.foodrescue.entity.User;
import com.foodrescue.enums.DonationStatus;
import com.foodrescue.enums.FoodCategory;
import com.foodrescue.exception.*;
import com.foodrescue.repository.FoodDonationRepository;
import com.foodrescue.repository.UserRepository;
import com.foodrescue.service.AuditLogService;
import com.foodrescue.service.FoodDonationService;
import com.foodrescue.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class FoodDonationServiceImpl implements FoodDonationService {

    private final FoodDonationRepository donationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    @CacheEvict(value = "availableDonations", allEntries = true)
    public DonationResponse createDonation(CreateDonationRequest request, Long donorId) {
        // Validate business rules
        if (request.getPickupEndTime().isBefore(request.getPickupStartTime())) {
            throw new BadRequestException("Pickup end time must be after pickup start time.");
        }
        if (request.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Expiry time must be in the future.");
        }

        User donor = userRepository.findById(donorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", donorId));

        FoodDonation donation = FoodDonation.builder()
                .donor(donor)
                .foodName(request.getFoodName())
                .foodCategory(request.getFoodCategory())
                .description(request.getDescription())
                .quantity(request.getQuantity())
                .quantityUnit(request.getQuantityUnit())
                .preparationTime(request.getPreparationTime())
                .expiryTime(request.getExpiryTime())
                .pickupStartTime(request.getPickupStartTime())
                .pickupEndTime(request.getPickupEndTime())
                .vegetarian(request.isVegetarian())
                .allergens(request.getAllergens())
                .packagingInformation(request.getPackagingInformation())
                .pickupAddress(request.getPickupAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .status(DonationStatus.AVAILABLE)
                .build();

        donation = donationRepository.save(donation);

        auditLogService.log(donorId, "DONATION_CREATED", "FoodDonation", donation.getId(),
                "Donor created donation: " + donation.getFoodName());

        log.info("Donation created by donor {}: {} (id={})", donorId, donation.getFoodName(), donation.getId());
        return toResponse(donation);
    }

    @Override
    @Transactional(readOnly = true)
    public DonationResponse getDonationById(Long id) {
        FoodDonation donation = donationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodDonation", id));
        return toResponse(donation);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "availableDonations", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public PagedResponse<DonationResponse> getAllDonations(
            DonationStatus status, FoodCategory category, Boolean vegetarian,
            Double minQty, Double maxQty, Pageable pageable) {

        Page<FoodDonation> page = donationRepository
                .findWithFilters(status, category, vegetarian, minQty, maxQty, pageable);
        return PagedResponse.of(page.map(this::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<DonationResponse> getMyDonations(Long donorId, DonationStatus status, Pageable pageable) {
        Page<FoodDonation> page;
        if (status != null) {
            page = donationRepository.findByDonorIdAndStatus(donorId, status, pageable);
        } else {
            page = donationRepository.findByDonorId(donorId, pageable);
        }
        return PagedResponse.of(page.map(this::toResponse));
    }

    @Override
    @Transactional
    @CacheEvict(value = "availableDonations", allEntries = true)
    public DonationResponse updateDonation(Long id, CreateDonationRequest request, Long donorId) {
        FoodDonation donation = donationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodDonation", id));

        if (!donation.getDonor().getId().equals(donorId)) {
            throw new UnauthorizedException("You can only edit your own donations.");
        }
        if (donation.getStatus() != DonationStatus.AVAILABLE) {
            throw new BadRequestException("Only AVAILABLE donations can be edited.");
        }

        donation.setFoodName(request.getFoodName());
        donation.setFoodCategory(request.getFoodCategory());
        donation.setDescription(request.getDescription());
        donation.setQuantity(request.getQuantity());
        donation.setQuantityUnit(request.getQuantityUnit());
        donation.setPreparationTime(request.getPreparationTime());
        donation.setExpiryTime(request.getExpiryTime());
        donation.setPickupStartTime(request.getPickupStartTime());
        donation.setPickupEndTime(request.getPickupEndTime());
        donation.setVegetarian(request.isVegetarian());
        donation.setAllergens(request.getAllergens());
        donation.setPackagingInformation(request.getPackagingInformation());
        donation.setPickupAddress(request.getPickupAddress());
        donation.setLatitude(request.getLatitude());
        donation.setLongitude(request.getLongitude());

        donation = donationRepository.save(donation);
        auditLogService.log(donorId, "DONATION_UPDATED", "FoodDonation", id, "Donation updated");
        return toResponse(donation);
    }

    @Override
    @Transactional
    @CacheEvict(value = "availableDonations", allEntries = true)
    public void cancelDonation(Long id, Long donorId) {
        FoodDonation donation = donationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodDonation", id));

        if (!donation.getDonor().getId().equals(donorId)) {
            throw new UnauthorizedException("You can only cancel your own donations.");
        }
        if (donation.getStatus() == DonationStatus.COLLECTED ||
                donation.getStatus() == DonationStatus.DELIVERED ||
                donation.getStatus() == DonationStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel a donation that is already collected or completed.");
        }

        donation.setStatus(DonationStatus.CANCELLED);
        donationRepository.save(donation);

        auditLogService.log(donorId, "DONATION_CANCELLED", "FoodDonation", id, "Donation cancelled by donor");
        log.info("Donation {} cancelled by donor {}", id, donorId);
    }

    /** Convert entity to response DTO. */
    public DonationResponse toResponse(FoodDonation d) {
        return DonationResponse.builder()
                .id(d.getId())
                .donorId(d.getDonor().getId())
                .donorName(d.getDonor().getFullName())
                .assignedNgoId(d.getAssignedNgo() != null ? d.getAssignedNgo().getId() : null)
                .assignedNgoName(d.getAssignedNgo() != null ? d.getAssignedNgo().getFullName() : null)
                .foodName(d.getFoodName())
                .foodCategory(d.getFoodCategory())
                .description(d.getDescription())
                .quantity(d.getQuantity())
                .quantityUnit(d.getQuantityUnit())
                .preparationTime(d.getPreparationTime())
                .expiryTime(d.getExpiryTime())
                .pickupStartTime(d.getPickupStartTime())
                .pickupEndTime(d.getPickupEndTime())
                .vegetarian(d.isVegetarian())
                .allergens(d.getAllergens())
                .packagingInformation(d.getPackagingInformation())
                .pickupAddress(d.getPickupAddress())
                .latitude(d.getLatitude())
                .longitude(d.getLongitude())
                .status(d.getStatus())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
