package com.foodrescue.dto.response;

import com.foodrescue.enums.PickupStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/** DTO representation of a Pickup entity. */
@Data
@Builder
public class PickupResponse {
    private Long id;
    private Long donationId;
    private String donationFoodName;
    private Long ngoId;
    private String ngoName;
    private Long volunteerId;
    private String volunteerName;
    private String pickupAddress;
    private String deliveryAddress;
    private LocalDateTime scheduledPickupTime;
    private LocalDateTime assignedAt;
    private LocalDateTime collectedAt;
    private LocalDateTime deliveredAt;
    private PickupStatus status;
    private String notes;
}
