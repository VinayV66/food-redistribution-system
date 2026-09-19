package com.foodrescue.entity;

import com.foodrescue.enums.PickupStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * A pickup task created when an NGO accepts a food donation.
 * Represents the physical collection and delivery of food.
 */
@Entity
@Table(name = "pickups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pickup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The donation this pickup is for (one-to-one: one donation = one pickup). */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_id", nullable = false, unique = true)
    private FoodDonation donation;

    /** The NGO that accepted this donation. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ngo_id", nullable = false)
    private User ngo;

    /** The volunteer assigned to collect the food (may be null initially). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volunteer_id")
    private User volunteer;

    @Column(nullable = false)
    private String pickupAddress;

    /** Where the food should be delivered (NGO's address). */
    private String deliveryAddress;

    private LocalDateTime scheduledPickupTime;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime assignedAt;

    private LocalDateTime collectedAt;
    private LocalDateTime deliveredAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PickupStatus status = PickupStatus.PENDING;

    private String notes;
}
