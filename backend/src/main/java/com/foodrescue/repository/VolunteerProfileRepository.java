package com.foodrescue.repository;

import com.foodrescue.entity.VolunteerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteerProfileRepository extends JpaRepository<VolunteerProfile, Long> {

    Optional<VolunteerProfile> findByUserId(Long userId);

    /** Find volunteers who are available for pickup tasks. */
    List<VolunteerProfile> findByAvailableTrue();
}
