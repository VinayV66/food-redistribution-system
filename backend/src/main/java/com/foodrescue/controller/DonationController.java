package com.foodrescue.controller;

import com.foodrescue.dto.request.CreateDonationRequest;
import com.foodrescue.dto.response.DonationResponse;
import com.foodrescue.dto.response.PagedResponse;
import com.foodrescue.enums.DonationStatus;
import com.foodrescue.enums.FoodCategory;
import com.foodrescue.repository.UserRepository;
import com.foodrescue.service.FoodDonationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * REST API for food donation CRUD and browsing.
 *
 * Public endpoints:
 *   GET /api/donations   — anyone can browse available donations
 *
 * Protected endpoints require JWT.
 */
@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
@Tag(name = "Donations", description = "Food donation management")
public class DonationController {

    private final FoodDonationService donationService;
    private final UserRepository userRepository;

    /**
     * Browse all donations with optional filters.
     * GET /api/donations?page=0&size=10&status=AVAILABLE&category=COOKED_FOOD
     */
    @GetMapping
    @Operation(summary = "Browse all donations with optional filters and pagination")
    public ResponseEntity<PagedResponse<DonationResponse>> getAllDonations(
            @RequestParam(required = false) DonationStatus status,
            @RequestParam(required = false) FoodCategory category,
            @RequestParam(required = false) Boolean vegetarian,
            @RequestParam(required = false) Double minQty,
            @RequestParam(required = false) Double maxQty,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageRequest = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return ResponseEntity.ok(
                donationService.getAllDonations(status, category, vegetarian, minQty, maxQty, pageRequest));
    }

    /** Get a specific donation by ID. */
    @GetMapping("/{id}")
    @Operation(summary = "Get donation details by ID")
    public ResponseEntity<DonationResponse> getDonationById(@PathVariable Long id) {
        return ResponseEntity.ok(donationService.getDonationById(id));
    }

    /** Create a new donation (DONOR only). */
    @PostMapping
    @PreAuthorize("hasRole('DONOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a new food donation (DONOR only)")
    public ResponseEntity<DonationResponse> createDonation(
            @Valid @RequestBody CreateDonationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long donorId = getUserId(userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(donationService.createDonation(request, donorId));
    }

    /** Get my own donations (DONOR only). */
    @GetMapping("/my")
    @PreAuthorize("hasRole('DONOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get my donations (DONOR only)")
    public ResponseEntity<PagedResponse<DonationResponse>> getMyDonations(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) DonationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long donorId = getUserId(userDetails);
        PageRequest pageRequest = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(donationService.getMyDonations(donorId, status, pageRequest));
    }

    /** Update a donation (DONOR only, only AVAILABLE donations). */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DONOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update a donation (DONOR only, only if AVAILABLE)")
    public ResponseEntity<DonationResponse> updateDonation(
            @PathVariable Long id,
            @Valid @RequestBody CreateDonationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long donorId = getUserId(userDetails);
        return ResponseEntity.ok(donationService.updateDonation(id, request, donorId));
    }

    /** Cancel a donation (DONOR only). */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DONOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cancel a donation (DONOR only)")
    public ResponseEntity<Void> cancelDonation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        donationService.cancelDonation(id, getUserId(userDetails));
        return ResponseEntity.noContent().build();
    }

    /** Helper: Get the database user ID from the JWT principal. */
    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow().getId();
    }
}
