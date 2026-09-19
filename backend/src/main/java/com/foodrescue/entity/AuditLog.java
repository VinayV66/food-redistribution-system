package com.foodrescue.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Audit trail for important system actions.
 * Stored in the database for admin review.
 */
@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who performed the action (null for system actions). */
    private Long userId;

    /** What happened, e.g. "DONATION_CREATED", "USER_BLOCKED". */
    @Column(nullable = false)
    private String action;

    /** Type of entity affected, e.g. "FoodDonation", "User". */
    private String entityType;

    /** ID of the affected entity. */
    private Long entityId;

    /** Human-readable description of the action. */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** IP address or client info (optional, for security). */
    private String clientInfo;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime timestamp;
}
