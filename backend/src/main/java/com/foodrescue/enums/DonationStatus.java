package com.foodrescue.enums;

/**
 * Full lifecycle of a food donation.
 *
 * AVAILABLE      → Donation posted; waiting for NGO to accept.
 * REQUESTED      → An NGO has requested this donation.
 * ACCEPTED       → NGO accepted; pickup will be created.
 * PICKUP_ASSIGNED → A volunteer has been assigned for pickup.
 * COLLECTED      → Volunteer has collected the food.
 * DELIVERED      → Food delivered to the NGO/beneficiaries.
 * COMPLETED      → Full workflow done.
 * EXPIRED        → Expiry time passed without being collected.
 * CANCELLED      → Donor cancelled the donation.
 */
public enum DonationStatus {
    AVAILABLE,
    REQUESTED,
    ACCEPTED,
    PICKUP_ASSIGNED,
    COLLECTED,
    DELIVERED,
    COMPLETED,
    EXPIRED,
    CANCELLED
}
