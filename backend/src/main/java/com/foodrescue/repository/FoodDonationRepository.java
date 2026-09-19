package com.foodrescue.repository;

import com.foodrescue.entity.FoodDonation;
import com.foodrescue.enums.DonationStatus;
import com.foodrescue.enums.FoodCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FoodDonationRepository extends JpaRepository<FoodDonation, Long> {

    /** All donations by a specific donor (for donor dashboard). */
    Page<FoodDonation> findByDonorId(Long donorId, Pageable pageable);

    /** Filter by status (e.g., show only AVAILABLE donations). */
    Page<FoodDonation> findByStatus(DonationStatus status, Pageable pageable);

    /** Filter by donor AND status (donor's own active donations). */
    Page<FoodDonation> findByDonorIdAndStatus(Long donorId, DonationStatus status, Pageable pageable);

    /** Complex filter for the search/browse feature. */
    @Query("SELECT d FROM FoodDonation d WHERE " +
           "(:status IS NULL OR d.status = :status) AND " +
           "(:category IS NULL OR d.foodCategory = :category) AND " +
           "(:vegetarian IS NULL OR d.vegetarian = :vegetarian) AND " +
           "(:minQty IS NULL OR d.quantity >= :minQty) AND " +
           "(:maxQty IS NULL OR d.quantity <= :maxQty)")
    Page<FoodDonation> findWithFilters(
            @Param("status") DonationStatus status,
            @Param("category") FoodCategory category,
            @Param("vegetarian") Boolean vegetarian,
            @Param("minQty") Double minQty,
            @Param("maxQty") Double maxQty,
            Pageable pageable);

    /** Used by the expiry scheduler: find donations that haven't been collected and are now expired. */
    @Query("SELECT d FROM FoodDonation d WHERE d.expiryTime < :now " +
           "AND d.status NOT IN ('COMPLETED', 'EXPIRED', 'CANCELLED', 'DELIVERED', 'COLLECTED')")
    List<FoodDonation> findExpiredDonations(@Param("now") LocalDateTime now);

    /** Admin dashboard: count by status. */
    long countByStatus(DonationStatus status);

    /** NGO: donations that have been accepted by a specific NGO. */
    Page<FoodDonation> findByAssignedNgoId(Long ngoId, Pageable pageable);

    /** Total quantity of food rescued (donated and completed). */
    @Query("SELECT COALESCE(SUM(d.quantity), 0) FROM FoodDonation d WHERE d.status = 'COMPLETED'")
    Double getTotalFoodRescued();

    /** Donor count of donations. */
    long countByDonorId(Long donorId);

    /** Donor food rescued. */
    @Query("SELECT COALESCE(SUM(d.quantity), 0) FROM FoodDonation d WHERE d.donor.id = :donorId AND d.status = 'COMPLETED'")
    Double getTotalFoodRescuedByDonor(@Param("donorId") Long donorId);
}
