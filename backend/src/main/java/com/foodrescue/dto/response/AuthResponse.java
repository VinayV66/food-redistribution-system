package com.foodrescue.dto.response;

import com.foodrescue.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Returned after successful login/registration. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType;     // Always "Bearer"
    private Long userId;
    private String email;
    private String fullName;
    private UserRole role;
}
