package com.foodrescue.exception;

/** Thrown when trying to move a donation/pickup to an invalid status. */
public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(String from, String to) {
        super("Cannot transition from " + from + " to " + to);
    }
}
