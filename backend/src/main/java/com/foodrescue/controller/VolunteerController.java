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

/**
 * API endpoints for volunteer operations:
 *   - Browse available pickups
 *   - Accept a pickup task
 *   - Mark collected / delivered
 */
@RestController
@RequestMapping("/api/volunteer")
@RequiredArgsConstructor
@Tag(name = "Volunteer", description = "Volunteer pickup task management")
public class VolunteerController {

    private final PickupService pickupService;
    private final UserRepository userRepository;

    /** View available pickup tasks (PENDING, no volunteer yet). */
    @GetMapping("/tasks")
    @PreAuthorize("hasRole('VOLUNTEER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "View available pickup tasks (VOLUNTEER only)")
    public ResponseEntity<PagedResponse<PickupResponse>> getAvailableTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                pickupService.getAvailablePickups(
                        PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "assignedAt"))));
    }

    /** View my assigned tasks. */
    @GetMapping("/my-tasks")
    @PreAuthorize("hasRole('VOLUNTEER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "View my assigned pickup tasks")
    public ResponseEntity<PagedResponse<PickupResponse>> getMyTasks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long volunteerId = getUserId(userDetails);
        return ResponseEntity.ok(
                pickupService.getPickupsForVolunteer(volunteerId,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "assignedAt"))));
    }

    /**
     * Accept a pickup task (volunteer assigns themselves, then accepts).
     * This is a two-step operation: assign → accept.
     */
    @PostMapping("/tasks/{id}/accept")
    @PreAuthorize("hasRole('VOLUNTEER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Accept a pickup task")
    public ResponseEntity<PickupResponse> acceptTask(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long volunteerId = getUserId(userDetails);
        pickupService.assignVolunteer(id, volunteerId);
        return ResponseEntity.ok(pickupService.acceptPickup(id, volunteerId));
    }

    /** Mark food as collected from the donor. */
    @PostMapping("/tasks/{id}/collect")
    @PreAuthorize("hasRole('VOLUNTEER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Mark food as collected")
    public ResponseEntity<PickupResponse> markCollected(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(pickupService.markCollected(id, getUserId(userDetails)));
    }

    /** Mark food as delivered to the NGO. */
    @PostMapping("/tasks/{id}/deliver")
    @PreAuthorize("hasRole('VOLUNTEER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Mark food as delivered")
    public ResponseEntity<PickupResponse> markDelivered(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(pickupService.markDelivered(id, getUserId(userDetails)));
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow().getId();
    }
}
