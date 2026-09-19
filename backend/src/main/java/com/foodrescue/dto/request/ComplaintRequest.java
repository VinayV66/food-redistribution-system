package com.foodrescue.dto.request;

import com.foodrescue.enums.ComplaintType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ComplaintRequest {
    @NotNull(message = "Complaint type is required")
    private ComplaintType type;

    @NotBlank(message = "Description is required")
    private String description;

    private Long reportedUserId;  // Optional
    private Long donationId;      // Optional
}
