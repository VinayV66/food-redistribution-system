package com.foodrescue.controller;

import com.foodrescue.dto.request.ComplaintRequest;
import com.foodrescue.entity.Complaint;
import com.foodrescue.entity.FoodDonation;
import com.foodrescue.entity.User;
import com.foodrescue.exception.ResourceNotFoundException;
import com.foodrescue.repository.ComplaintRepository;
import com.foodrescue.repository.FoodDonationRepository;
import com.foodrescue.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Complaints", description = "User complaint submission")
public class ComplaintController {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final FoodDonationRepository donationRepository;

    @PostMapping
    @Operation(summary = "File a complaint")
    public ResponseEntity<Complaint> fileComplaint(
            @Valid @RequestBody ComplaintRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User reporter = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        User reportedUser = request.getReportedUserId() != null
                ? userRepository.findById(request.getReportedUserId()).orElse(null) : null;

        FoodDonation donation = request.getDonationId() != null
                ? donationRepository.findById(request.getDonationId()).orElse(null) : null;

        Complaint complaint = Complaint.builder()
                .reporter(reporter)
                .reportedUser(reportedUser)
                .donation(donation)
                .type(request.getType())
                .description(request.getDescription())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(complaintRepository.save(complaint));
    }

    @GetMapping("/my")
    @Operation(summary = "Get my filed complaints")
    public ResponseEntity<Page<Complaint>> getMyComplaints(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = userRepository.findByEmail(userDetails.getUsername()).orElseThrow().getId();
        return ResponseEntity.ok(
                complaintRepository.findByReporterId(userId,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))));
    }
}
