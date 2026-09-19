package com.foodrescue.entity;

import com.foodrescue.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Core user entity — used for authentication.
 * Each user has exactly ONE role and ONE profile (Donor / NGO / Volunteer).
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    /** BCrypt-hashed password — NEVER store plain text. */
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    /** Whether this account is active. Admin can set to false to block user. */
    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    /** Whether this account has been blocked by an admin. */
    @Column(nullable = false)
    @Builder.Default
    private boolean blocked = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
