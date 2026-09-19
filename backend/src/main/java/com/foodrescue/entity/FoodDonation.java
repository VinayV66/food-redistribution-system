package com.foodrescue.entity;

import com.foodrescue.enums.DonationStatus;
import com.foodrescue.enums.FoodCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Represents a food donation posted by a DONOR.
 *
 * Lifecycle:
 *  AVAILABLE → REQUESTED → ACCEPTED → PICKUP_ASSIGNED
 *            → COLLECTED → DELIVERED → COMPLETED
 *
 *  Or: AVAILABLE → EXPIRED  (if not picked up in time)
 *  Or: AVAILABLE → CANCELLED (donor cancels)
 */
@Entity
@Table(name = "food_donations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodDonation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The donor who created this donation. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id", nullable = false)
    private User donor;

    /** NGO that accepted this donation (set when status changes to ACCEPTED). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_ngo_id")
    private User assignedNgo;

    @NotBlank(message = "Food name is required")
    @Column(nullable = false)
    private String foodName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FoodCategory foodCategory;

    private String description;

    @Positive(message = "Quantity must be a positive number")
    @Column(nullable = false)
    private Double quantity;

    /** kg, litres, servings, boxes, etc. */
    @NotBlank(message = "Quantity unit is required")
    private String quantityUnit;

    /** When the food was prepared. */
    private LocalDateTime preparationTime;

    /** Food MUST be picked up before this time — used for expiry checks. */
    @Future(message = "Expiry time must be in the future")
    @Column(nullable = false)
    private LocalDateTime expiryTime;

    /** Earliest time the donor is available for pickup. */
    @Column(nullable = false)
    private LocalDateTime pickupStartTime;

    /** Latest time for pickup. Must be after pickupStartTime. */
    @Column(nullable = false)
    private LocalDateTime pickupEndTime;

    private boolean vegetarian;

    /** Comma-separated allergens, e.g. "nuts, dairy" */
    private String allergens;

    private String packagingInformation;

    @NotBlank(message = "Pickup address is required")
    @Column(nullable = false)
    private String pickupAddress;

    /** GPS coordinates for distance-based matching. */
    private Double latitude;
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DonationStatus status = DonationStatus.AVAILABLE;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
