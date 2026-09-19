package com.foodrescue.dto.response;

import com.foodrescue.enums.DonationStatus;
import com.foodrescue.enums.FoodCategory;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/** Safe DTO representation of a FoodDonation (returned to frontend). */
@Data
@Builder
public class DonationResponse {
    private Long id;
    private Long donorId;
    private String donorName;
    private Long assignedNgoId;
    private String assignedNgoName;
    private String foodName;
    private FoodCategory foodCategory;
    private String description;
    private Double quantity;
    private String quantityUnit;
    private LocalDateTime preparationTime;
    private LocalDateTime expiryTime;
    private LocalDateTime pickupStartTime;
    private LocalDateTime pickupEndTime;
    private boolean vegetarian;
    private String allergens;
    private String packagingInformation;
    private String pickupAddress;
    private Double latitude;
    private Double longitude;
    private DonationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
