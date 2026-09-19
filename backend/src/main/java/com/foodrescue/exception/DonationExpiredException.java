package com.foodrescue.exception;

/** Thrown when someone tries to interact with an expired donation. */
public class DonationExpiredException extends RuntimeException {
    public DonationExpiredException(String message) {
        super(message);
    }
}
