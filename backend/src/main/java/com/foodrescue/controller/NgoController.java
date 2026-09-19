package com.foodrescue.controller;

import com.foodrescue.dto.response.DonationResponse;
import com.foodrescue.dto.response.PagedResponse;
import com.foodrescue.dto.response.PickupResponse;
import com.foodrescue.entity.NgoProfile;
import com.foodrescue.enums.DonationStatus;
import com.foodrescue.exception.BadRequestException;
import com.foodrescue.exception.ResourceNotFoundException;
import com.foodrescue.repository.NgoProfileRepository;
import com.foodrescue.repository.UserRepository;
import com.foodrescue.service.FoodDonationService;
import com.foodrescue.service.PickupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API endpoints for NGO operations:
 *   - Browse available donations
 *   - Accept a donation (creates a pickup)
 *   - View pickup history
 */
@RestController
@RequestMapping("/api/ngo")
@RequiredArgsConstructor
@Tag(name = "NGO", description = "NGO donation browsing and acceptance")
public class NgoController {

    private final FoodDonationService donationService;
    private final PickupService pickupService;
    private final UserRepository userRepository;
    private final NgoProfileRepository ngoProfileRepository;

    /** Get all approved NGO profiles (public). */
    @GetMapping
    @Operation(summary = "List all approved NGOs")
    public ResponseEntity<List<NgoProfile>> getAllNgos() {
        return ResponseEntity.ok(ngoProfileRepository.findByApprovedTrue());
    }

    /** Browse available donations (NGO sees AVAILABLE donations sorted by expiry). */
    @GetMapping("/available-donations")
    @PreAuthorize("hasRole('NGO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Browse available food donations (NGO only)")
    public ResponseEntity<PagedResponse<DonationResponse>> browseAvailableDonations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageRequest = PageRequest.of(page, size,
                Sort.by(Sort.Direction.ASC, "expiryTime"));
        return ResponseEntity.ok(
                donationService.getAllDonations(
                        DonationStatus.AVAILABLE, null, null, null, null, pageRequest));
    }

    /**
     * Accept a donation: creates a pickup and updates donation status.
     * POST /api/ngo/accept-donation/{donationId}
     */
    @PostMapping("/accept-donation/{donationId}")
    @PreAuthorize("hasRole('NGO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Accept a food donation (NGO only)")
    public ResponseEntity<PickupResponse> acceptDonation(
            @PathVariable Long donationId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long ngoUserId = getUserId(userDetails);

        // Verify the NGO is approved
        NgoProfile ngoProfile = ngoProfileRepository.findByUserId(ngoUserId)
                .orElseThrow(() -> new ResourceNotFoundException("NGO profile not found"));
        if (!ngoProfile.isApproved()) {
            throw new BadRequestException("Your NGO account is pending admin approval.");
        }

        return ResponseEntity.ok(pickupService.createPickup(donationId, ngoUserId));
    }

    /** Get this NGO's pickup history. */
    @GetMapping("/my-pickups")
    @PreAuthorize("hasRole('NGO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get NGO's pickup history")
    public ResponseEntity<PagedResponse<PickupResponse>> getMyPickups(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long ngoId = getUserId(userDetails);
        return ResponseEntity.ok(
                pickupService.getPickupsForNgo(ngoId,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "assignedAt"))));
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow().getId();
    }
}
