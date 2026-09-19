package com.foodrescue.service.impl;

import com.foodrescue.dto.request.LoginRequest;
import com.foodrescue.dto.request.RegisterRequest;
import com.foodrescue.dto.response.AuthResponse;
import com.foodrescue.entity.*;
import com.foodrescue.enums.UserRole;
import com.foodrescue.exception.BadRequestException;
import com.foodrescue.repository.*;
import com.foodrescue.security.JwtTokenProvider;
import com.foodrescue.service.AuthService;
import com.foodrescue.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of authentication operations.
 *
 * REGISTER: Validate → Hash password → Save user → Create profile → Return JWT.
 * LOGIN:    Authenticate → Generate JWT → Return JWT.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final DonorProfileRepository donorProfileRepository;
    private final NgoProfileRepository ngoProfileRepository;
    private final VolunteerProfileRepository volunteerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Step 1: Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email address is already registered: " + request.getEmail());
        }

        // Step 2: Prevent registering as ADMIN through public API
        if (request.getRole() == UserRole.ADMIN) {
            throw new BadRequestException("Cannot register as ADMIN through this endpoint.");
        }

        // Step 3: Create and save the User entity
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))  // BCrypt hash
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(request.getRole())
                .enabled(true)
                .blocked(false)
                .build();
        userRepository.save(user);

        // Step 4: Create the role-specific profile
        createProfile(user, request);

        // Step 5: Auto-login to get a JWT
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        String token = tokenProvider.generateToken(authentication);

        // Step 6: Audit log
        auditLogService.log(user.getId(), "USER_REGISTERED", "User", user.getId(),
                "New user registered: " + user.getEmail() + " with role " + user.getRole());

        log.info("New user registered: {} with role {}", user.getEmail(), user.getRole());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Spring Security handles authentication (checks password, blocked status, etc.)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String token = tokenProvider.generateToken(authentication);

        // Load user details for the response
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        auditLogService.log(user.getId(), "USER_LOGIN", "User", user.getId(),
                "User logged in: " + user.getEmail());

        log.info("User logged in: {}", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    /** Create the appropriate profile based on the user's role. */
    private void createProfile(User user, RegisterRequest request) {
        switch (user.getRole()) {
            case DONOR -> {
                DonorProfile profile = DonorProfile.builder()
                        .user(user)
                        .organizationName(request.getOrganizationName())
                        .organizationType(request.getOrganizationType())
                        .address(request.getAddress())
                        .latitude(request.getLatitude())
                        .longitude(request.getLongitude())
                        .build();
                donorProfileRepository.save(profile);
            }
            case NGO -> {
                NgoProfile profile = NgoProfile.builder()
                        .user(user)
                        .ngoName(request.getNgoName() != null ? request.getNgoName() : request.getOrganizationName())
                        .registrationNumber(request.getRegistrationNumber())
                        .address(request.getAddress())
                        .latitude(request.getLatitude())
                        .longitude(request.getLongitude())
                        .approved(false)  // Must be approved by admin
                        .build();
                ngoProfileRepository.save(profile);
            }
            case VOLUNTEER -> {
                VolunteerProfile profile = VolunteerProfile.builder()
                        .user(user)
                        .address(request.getAddress())
                        .latitude(request.getLatitude())
                        .longitude(request.getLongitude())
                        .available(true)
                        .build();
                volunteerProfileRepository.save(profile);
            }
            default -> throw new BadRequestException("Unknown role: " + user.getRole());
        }
    }
}
