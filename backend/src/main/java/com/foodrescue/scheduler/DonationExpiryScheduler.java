package com.foodrescue.scheduler;

import com.foodrescue.entity.FoodDonation;
import com.foodrescue.enums.DonationStatus;
import com.foodrescue.enums.NotificationType;
import com.foodrescue.repository.FoodDonationRepository;
import com.foodrescue.service.AuditLogService;
import com.foodrescue.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled job that runs every minute to expire overdue donations.
 *
 * It finds donations whose expiryTime has passed and are still in
 * an active status (AVAILABLE, REQUESTED, ACCEPTED).
 *
 * This prevents expired food from being accepted or collected.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DonationExpiryScheduler {

    private final FoodDonationRepository donationRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    /**
     * Runs every 60 seconds.
     * fixedRate = 60000 milliseconds = 1 minute.
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    @CacheEvict(value = "availableDonations", allEntries = true)
    public void expireOverdueDonations() {
        List<FoodDonation> expiredDonations =
                donationRepository.findExpiredDonations(LocalDateTime.now());

        if (expiredDonations.isEmpty()) return;

        log.info("Expiry scheduler: found {} donation(s) to expire.", expiredDonations.size());

        for (FoodDonation donation : expiredDonations) {
            donation.setStatus(DonationStatus.EXPIRED);
            donationRepository.save(donation);

            // Notify donor that their donation expired
            try {
                notificationService.sendNotification(
                        donation.getDonor().getId(),
                        "Donation Expired",
                        "Your donation '" + donation.getFoodName() +
                                "' has expired and was not collected in time.",
                        NotificationType.DONATION_EXPIRED);
            } catch (Exception e) {
                log.warn("Failed to send expiry notification for donation {}: {}",
                        donation.getId(), e.getMessage());
            }

            auditLogService.log(null, "DONATION_EXPIRED", "FoodDonation", donation.getId(),
                    "Donation auto-expired: " + donation.getFoodName());
        }

        log.info("Expiry scheduler: {} donation(s) expired.", expiredDonations.size());
    }
}
