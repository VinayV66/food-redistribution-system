package com.foodrescue.repository;

import com.foodrescue.entity.Pickup;
import com.foodrescue.enums.PickupStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PickupRepository extends JpaRepository<Pickup, Long> {

    /** Find the pickup for a specific donation (one-to-one). */
    Optional<Pickup> findByDonationId(Long donationId);

    /** All pickups for an NGO. */
    Page<Pickup> findByNgoId(Long ngoId, Pageable pageable);

    /** All pickups assigned to a volunteer. */
    Page<Pickup> findByVolunteerId(Long volunteerId, Pageable pageable);

    /** Available pickups (no volunteer yet). */
    Page<Pickup> findByStatus(PickupStatus status, Pageable pageable);

    /** Volunteer: pickups they can accept (PENDING = no volunteer assigned). */
    Page<Pickup> findByVolunteerIsNullAndStatus(PickupStatus status, Pageable pageable);

    /** Count pickups for an NGO by status. */
    long countByNgoIdAndStatus(Long ngoId, PickupStatus status);

    /** Count pickups for a volunteer by status. */
    long countByVolunteerIdAndStatus(Long volunteerId, PickupStatus status);
}
