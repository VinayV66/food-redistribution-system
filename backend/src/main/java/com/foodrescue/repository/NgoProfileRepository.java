package com.foodrescue.repository;

import com.foodrescue.entity.NgoProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NgoProfileRepository extends JpaRepository<NgoProfile, Long> {

    Optional<NgoProfile> findByUserId(Long userId);

    /** Only approved NGOs should receive donation matches. */
    List<NgoProfile> findByApprovedTrue();

    /** Admin: list NGOs pending approval. */
    Page<NgoProfile> findByApprovedFalse(Pageable pageable);
}
