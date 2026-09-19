package com.foodrescue.config;

import com.foodrescue.entity.DonorProfile;
import com.foodrescue.entity.NgoProfile;
import com.foodrescue.entity.User;
import com.foodrescue.entity.VolunteerProfile;
import com.foodrescue.enums.UserRole;
import com.foodrescue.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Runs once on application startup to create sample development accounts.
 *
 * Test accounts created:
 *   admin@foodrescue.com     / Admin@123   → ADMIN
 *   donor@foodrescue.com     / Donor@123   → DONOR
 *   ngo@foodrescue.com       / Ngo@12345   → NGO (pre-approved)
 *   volunteer@foodrescue.com / Vol@12345   → VOLUNTEER
 *
 * These accounts are only created if they don't already exist.
 * Safe to run multiple times (idempotent).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DonorProfileRepository donorProfileRepository;
    private final NgoProfileRepository ngoProfileRepository;
    private final VolunteerProfileRepository volunteerProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        createAdminIfNotExists();
        createDonorIfNotExists();
        createNgoIfNotExists();
        createVolunteerIfNotExists();
        log.info("✅ Sample development accounts are ready.");
    }

    private void createAdminIfNotExists() {
        if (!userRepository.existsByEmail("admin@foodrescue.com")) {
            User admin = User.builder()
                    .email("admin@foodrescue.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .fullName("System Admin")
                    .phone("9000000001")
                    .role(UserRole.ADMIN)
                    .enabled(true)
                    .blocked(false)
                    .build();
            userRepository.save(admin);
            log.info("Created admin account: admin@foodrescue.com / Admin@123");
        }
    }

    private void createDonorIfNotExists() {
        if (!userRepository.existsByEmail("donor@foodrescue.com")) {
            User donor = User.builder()
                    .email("donor@foodrescue.com")
                    .password(passwordEncoder.encode("Donor@123"))
                    .fullName("Fresh Bites Restaurant")
                    .phone("9000000002")
                    .role(UserRole.DONOR)
                    .enabled(true)
                    .blocked(false)
                    .build();
            donor = userRepository.save(donor);

            DonorProfile profile = DonorProfile.builder()
                    .user(donor)
                    .organizationName("Fresh Bites Restaurant")
                    .organizationType("Restaurant")
                    .address("123 MG Road, Bangalore")
                    .latitude(12.9716)
                    .longitude(77.5946)
                    .verified(true)
                    .build();
            donorProfileRepository.save(profile);
            log.info("Created donor account: donor@foodrescue.com / Donor@123");
        }
    }

    private void createNgoIfNotExists() {
        if (!userRepository.existsByEmail("ngo@foodrescue.com")) {
            User ngo = User.builder()
                    .email("ngo@foodrescue.com")
                    .password(passwordEncoder.encode("Ngo@12345"))
                    .fullName("Hope Foundation")
                    .phone("9000000003")
                    .role(UserRole.NGO)
                    .enabled(true)
                    .blocked(false)
                    .build();
            ngo = userRepository.save(ngo);

            NgoProfile profile = NgoProfile.builder()
                    .user(ngo)
                    .ngoName("Hope Foundation")
                    .registrationNumber("NGO/2024/001")
                    .address("456 Nehru Street, Bangalore")
                    .latitude(12.9750)
                    .longitude(77.5900)
                    .dailyCapacity(100)
                    .approved(true)  // Pre-approved for testing
                    .build();
            ngoProfileRepository.save(profile);
            log.info("Created NGO account: ngo@foodrescue.com / Ngo@12345");
        }
    }

    private void createVolunteerIfNotExists() {
        if (!userRepository.existsByEmail("volunteer@foodrescue.com")) {
            User volunteer = User.builder()
                    .email("volunteer@foodrescue.com")
                    .password(passwordEncoder.encode("Vol@12345"))
                    .fullName("Raj Kumar")
                    .phone("9000000004")
                    .role(UserRole.VOLUNTEER)
                    .enabled(true)
                    .blocked(false)
                    .build();
            volunteer = userRepository.save(volunteer);

            VolunteerProfile profile = VolunteerProfile.builder()
                    .user(volunteer)
                    .address("789 Gandhi Nagar, Bangalore")
                    .latitude(12.9700)
                    .longitude(77.6000)
                    .vehicleType("Bike")
                    .available(true)
                    .build();
            volunteerProfileRepository.save(profile);
            log.info("Created volunteer account: volunteer@foodrescue.com / Vol@12345");
        }
    }
}
