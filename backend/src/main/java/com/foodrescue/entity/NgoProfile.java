package com.foodrescue.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Extended profile for NGO users.
 * Linked 1-to-1 with the User entity.
 * NGOs need admin approval before they can accept donations.
 */
@Entity
@Table(name = "ngo_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NgoProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String ngoName;
    private String registrationNumber;
    private String address;
    private Double latitude;
    private Double longitude;
    private String description;
    private String contactPerson;
    private String website;

    /**
     * How many people / meals the NGO can serve per day.
     * Used by the matching algorithm.
     */
    private Integer dailyCapacity;

    /** Admin must approve NGO before it can operate. */
    @Column(nullable = false)
    @Builder.Default
    private boolean approved = false;

    /** Rejection reason if admin rejects the NGO application. */
    private String rejectionReason;
}
