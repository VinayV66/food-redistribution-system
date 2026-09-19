package com.foodrescue.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Extended profile for DONOR users.
 * Linked 1-to-1 with the User entity.
 */
@Entity
@Table(name = "donor_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** One donor profile belongs to exactly one user. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /** Type of establishment: Restaurant, Hotel, Bakery, etc. */
    private String organizationName;
    private String organizationType;  // Restaurant, Hotel, Bakery, Event Organizer, etc.
    private String address;
    private Double latitude;
    private Double longitude;
    private String description;

    /** Total donations this donor has made (denormalized counter for quick dashboards). */
    @Column(nullable = false)
    @Builder.Default
    private int totalDonations = 0;

    /** Whether admin has verified/approved this donor. */
    @Column(nullable = false)
    @Builder.Default
    private boolean verified = false;
}
