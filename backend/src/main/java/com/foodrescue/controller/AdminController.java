package com.foodrescue.controller;

import com.foodrescue.dto.response.*;
import com.foodrescue.entity.AuditLog;
import com.foodrescue.entity.Complaint;
import com.foodrescue.entity.NgoProfile;
import com.foodrescue.entity.User;
import com.foodrescue.enums.ComplaintStatus;
import com.foodrescue.enums.DonationStatus;
import com.foodrescue.enums.NotificationType;
import com.foodrescue.enums.UserRole;
import com.foodrescue.exception.ResourceNotFoundException;
import com.foodrescue.repository.*;
import com.foodrescue.service.AuditLogService;
import com.foodrescue.service.FoodDonationService;
import com.foodrescue.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Admin-only endpoints:
 *   - Dashboard statistics
 *   - User management (block/unblock)
 *   - NGO approval/rejection
 *   - Complaint management
 *   - Audit log viewing
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin management endpoints")
public class AdminController {

    private final UserRepository userRepository;
    private final FoodDonationRepository donationRepository;
    private final PickupRepository pickupRepository;
    private final NgoProfileRepository ngoProfileRepository;
    private final ComplaintRepository complaintRepository;
    private final AuditLogRepository auditLogRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    /** Admin dashboard statistics. */
    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard statistics")
    public ResponseEntity<DashboardStatsResponse> getDashboard() {
        DashboardStatsResponse stats = DashboardStatsResponse.builder()
                .totalUsers(userRepository.count())
                .totalDonors(userRepository.countByRole(UserRole.DONOR))
                .totalNgos(userRepository.countByRole(UserRole.NGO))
                .totalVolunteers(userRepository.countByRole(UserRole.VOLUNTEER))
                .totalDonations(donationRepository.count())
                .availableDonations(donationRepository.countByStatus(DonationStatus.AVAILABLE))
                .completedDonations(donationRepository.countByStatus(DonationStatus.COMPLETED))
                .expiredDonations(donationRepository.countByStatus(DonationStatus.EXPIRED))
                .cancelledDonations(donationRepository.countByStatus(DonationStatus.CANCELLED))
                .totalFoodRescuedKg(donationRepository.getTotalFoodRescued() != null ?
                        donationRepository.getTotalFoodRescued() : 0.0)
                .activePickups(pickupRepository.count())
                .pendingNgoApprovals(ngoProfileRepository.findByApprovedFalse(
                        PageRequest.of(0, 1)).getTotalElements())
                .build();
        return ResponseEntity.ok(stats);
    }

    /** List all users with pagination and optional role/search filter. */
    @GetMapping("/users")
    @Operation(summary = "List all users")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) String search) {

        Page<User> users;
        if (search != null && !search.isBlank()) {
            users = userRepository.searchUsers(search, PageRequest.of(page, size));
        } else if (role != null) {
            users = userRepository.findByRole(role, PageRequest.of(page, size));
        } else {
            users = userRepository.findAll(PageRequest.of(page, size,
                    Sort.by(Sort.Direction.DESC, "createdAt")));
        }
        return ResponseEntity.ok(users);
    }

    /** Block a user account. */
    @PutMapping("/users/{id}/block")
    @Operation(summary = "Block a user")
    public ResponseEntity<Map<String, String>> blockUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails adminDetails) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.setBlocked(true);
        userRepository.save(user);

        Long adminId = userRepository.findByEmail(adminDetails.getUsername()).orElseThrow().getId();
        auditLogService.log(adminId, "USER_BLOCKED", "User", id,
                "Admin blocked user: " + user.getEmail());
        return ResponseEntity.ok(Map.of("message", "User blocked successfully"));
    }

    /** Unblock a user account. */
    @PutMapping("/users/{id}/unblock")
    @Operation(summary = "Unblock a user")
    public ResponseEntity<Map<String, String>> unblockUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.setBlocked(false);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User unblocked successfully"));
    }

    /** List NGOs pending approval. */
    @GetMapping("/ngo-approvals")
    @Operation(summary = "List NGOs pending approval")
    public ResponseEntity<Page<NgoProfile>> getPendingNgos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                ngoProfileRepository.findByApprovedFalse(PageRequest.of(page, size)));
    }

    /** Approve an NGO — they can now accept donations. */
    @PutMapping("/ngos/{userId}/approve")
    @Operation(summary = "Approve an NGO")
    public ResponseEntity<Map<String, String>> approveNgo(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails adminDetails) {

        NgoProfile profile = ngoProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("NGO profile for user", userId));
        profile.setApproved(true);
        profile.setRejectionReason(null);
        ngoProfileRepository.save(profile);

        notificationService.sendNotification(userId,
                "NGO Application Approved!",
                "Congratulations! Your NGO has been approved. You can now accept food donations.",
                NotificationType.NGO_APPROVED);

        Long adminId = userRepository.findByEmail(adminDetails.getUsername()).orElseThrow().getId();
        auditLogService.log(adminId, "NGO_APPROVED", "NgoProfile", profile.getId(),
                "Admin approved NGO for user: " + userId);
        return ResponseEntity.ok(Map.of("message", "NGO approved successfully"));
    }

    /** Reject an NGO application. */
    @PutMapping("/ngos/{userId}/reject")
    @Operation(summary = "Reject an NGO application")
    public ResponseEntity<Map<String, String>> rejectNgo(
            @PathVariable Long userId,
            @RequestBody Map<String, String> body) {

        NgoProfile profile = ngoProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("NGO profile for user", userId));
        profile.setApproved(false);
        profile.setRejectionReason(body.get("reason"));
        ngoProfileRepository.save(profile);

        notificationService.sendNotification(userId,
                "NGO Application Rejected",
                "Your NGO application was rejected. Reason: " + body.getOrDefault("reason", "Not specified"),
                NotificationType.NGO_REJECTED);

        return ResponseEntity.ok(Map.of("message", "NGO rejected"));
    }

    /** List all complaints (optionally filter by status). */
    @GetMapping("/complaints")
    @Operation(summary = "List all complaints")
    public ResponseEntity<Page<Complaint>> getComplaints(
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Complaint> complaints;
        if (status != null) {
            complaints = complaintRepository.findByStatus(status, PageRequest.of(page, size));
        } else {
            complaints = complaintRepository.findAll(PageRequest.of(page, size,
                    Sort.by(Sort.Direction.DESC, "createdAt")));
        }
        return ResponseEntity.ok(complaints);
    }

    /** Resolve a complaint. */
    @PutMapping("/complaints/{id}/resolve")
    @Operation(summary = "Resolve a complaint")
    public ResponseEntity<Map<String, String>> resolveComplaint(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", id));
        complaint.setStatus(ComplaintStatus.RESOLVED);
        complaint.setResolutionNote(body.get("note"));
        complaintRepository.save(complaint);
        return ResponseEntity.ok(Map.of("message", "Complaint resolved"));
    }

    /** View all audit logs (most recent first). */
    @GetMapping("/audit-logs")
    @Operation(summary = "View audit logs")
    public ResponseEntity<Page<AuditLog>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                auditLogRepository.findAll(PageRequest.of(page, size,
                        Sort.by(Sort.Direction.DESC, "timestamp"))));
    }
}
