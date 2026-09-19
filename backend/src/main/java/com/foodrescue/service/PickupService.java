package com.foodrescue.service;

import com.foodrescue.dto.response.PagedResponse;
import com.foodrescue.dto.response.PickupResponse;
import com.foodrescue.enums.PickupStatus;
import org.springframework.data.domain.Pageable;

public interface PickupService {
    PickupResponse createPickup(Long donationId, Long ngoId);
    PickupResponse getPickupById(Long id);
    PagedResponse<PickupResponse> getPickupsForNgo(Long ngoId, Pageable pageable);
    PagedResponse<PickupResponse> getPickupsForVolunteer(Long volunteerId, Pageable pageable);
    PagedResponse<PickupResponse> getAvailablePickups(Pageable pageable);
    PickupResponse assignVolunteer(Long pickupId, Long volunteerId);
    PickupResponse acceptPickup(Long pickupId, Long volunteerId);
    PickupResponse markCollected(Long pickupId, Long volunteerId);
    PickupResponse markDelivered(Long pickupId, Long volunteerId);
    PickupResponse cancelPickup(Long pickupId, Long requestingUserId);
}
