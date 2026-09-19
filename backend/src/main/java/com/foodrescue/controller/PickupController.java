package com.foodrescue.controller;

import com.foodrescue.dto.response.PagedResponse;
import com.foodrescue.dto.response.PickupResponse;
import com.foodrescue.repository.UserRepository;
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

@RestController
@RequestMapping("/api/pickups")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Pickups", description = "Pickup management endpoints")
public class PickupController {

    private final PickupService pickupService;
    private final UserRepository userRepository;

    @GetMapping("/{id}")
    @Operation(summary = "Get pickup details by ID")
    public ResponseEntity<PickupResponse> getPickupById(@PathVariable Long id) {
        return ResponseEntity.ok(pickupService.getPickupById(id));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('NGO', 'ADMIN')")
    @Operation(summary = "Cancel a pickup")
    public ResponseEntity<PickupResponse> cancelPickup(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userRepository.findByEmail(userDetails.getUsername()).orElseThrow().getId();
        return ResponseEntity.ok(pickupService.cancelPickup(id, userId));
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('NGO', 'ADMIN')")
    @Operation(summary = "Assign a volunteer to a pickup")
    public ResponseEntity<PickupResponse> assignVolunteer(
            @PathVariable Long id,
            @RequestParam Long volunteerId) {
        return ResponseEntity.ok(pickupService.assignVolunteer(id, volunteerId));
    }
}
