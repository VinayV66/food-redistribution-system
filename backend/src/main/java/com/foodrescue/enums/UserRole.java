package com.foodrescue.enums;

/**
 * The four user roles in the system.
 * Spring Security uses these as GrantedAuthority values.
 */
public enum UserRole {
    ADMIN,
    DONOR,
    NGO,
    VOLUNTEER
}
