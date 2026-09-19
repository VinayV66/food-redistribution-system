package com.foodrescue.dto.request;

import com.foodrescue.enums.UserRole;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO for the POST /api/auth/register endpoint.
 * Validation annotations ensure only valid data reaches the service layer.
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotNull(message = "Role is required")
    private UserRole role;

    // Optional profile fields
    private String organizationName;   // For donors/NGOs
    private String organizationType;   // Donor: Restaurant, Hotel, etc.
    private String ngoName;            // For NGOs
    private String registrationNumber; // For NGOs
    private String address;
    private Double latitude;
    private Double longitude;
}
