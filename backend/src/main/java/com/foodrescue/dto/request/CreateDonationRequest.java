package com.foodrescue.dto.request;

import com.foodrescue.enums.FoodCategory;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

/** DTO for POST /api/donations — creating a new food donation. */
@Data
public class CreateDonationRequest {

    @NotBlank(message = "Food name is required")
    private String foodName;

    @NotNull(message = "Food category is required")
    private FoodCategory foodCategory;

    private String description;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be a positive number")
    private Double quantity;

    @NotBlank(message = "Quantity unit is required")
    private String quantityUnit;

    private LocalDateTime preparationTime;

    @NotNull(message = "Expiry time is required")
    @Future(message = "Expiry time must be in the future")
    private LocalDateTime expiryTime;

    @NotNull(message = "Pickup start time is required")
    private LocalDateTime pickupStartTime;

    @NotNull(message = "Pickup end time is required")
    private LocalDateTime pickupEndTime;

    private boolean vegetarian;
    private String allergens;
    private String packagingInformation;

    @NotBlank(message = "Pickup address is required")
    private String pickupAddress;

    private Double latitude;
    private Double longitude;
}
