package com.foodrescue.enums;

/**
 * Status of a pickup task:
 *
 * PENDING   → Pickup record created; no volunteer assigned yet.
 * ASSIGNED  → A volunteer has been assigned.
 * ACCEPTED  → Volunteer accepted the task.
 * COLLECTED → Volunteer has collected the food.
 * DELIVERED → Food has been delivered.
 * CANCELLED → Pickup was cancelled.
 */
public enum PickupStatus {
    PENDING,
    ASSIGNED,
    ACCEPTED,
    COLLECTED,
    DELIVERED,
    CANCELLED
}
