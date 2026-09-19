package com.foodrescue.dto.response;

import com.foodrescue.enums.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/** Safe representation of a User — never exposes the password. */
@Data
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private UserRole role;
    private boolean enabled;
    private boolean blocked;
    private LocalDateTime createdAt;
}
