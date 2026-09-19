package com.foodrescue.exception;

/** Thrown when a user tries to perform an action they don't have permission for. */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
