package com.foodrescue.repository;

import com.foodrescue.entity.User;
import com.foodrescue.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for the User entity.
 * Spring Data JPA automatically implements these methods at runtime.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Used during login to look up a user by email. */
    Optional<User> findByEmail(String email);

    /** Check uniqueness before registration. */
    boolean existsByEmail(String email);

    /** Find all users with a specific role (for admin views). */
    Page<User> findByRole(UserRole role, Pageable pageable);

    /** Count users by role (for dashboard statistics). */
    long countByRole(UserRole role);

    /** Search by name or email (admin user management). */
    @Query("SELECT u FROM User u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<User> searchUsers(String query, Pageable pageable);
}
