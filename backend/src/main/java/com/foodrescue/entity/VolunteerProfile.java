package com.foodrescue.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Extended profile for VOLUNTEER users.
 * Linked 1-to-1 with the User entity.
 */
@Entity
@Table(name = "volunteer_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String address;
    private Double latitude;
    private Double longitude;
    private String vehicleType;  // Bike, Car, Walk, etc.
    private boolean available;

    /** Total number of successful deliveries. */
    @Column(nullable = false)
    @Builder.Default
    private int totalDeliveries = 0;
}
