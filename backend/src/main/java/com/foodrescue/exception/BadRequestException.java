package com.foodrescue.exception;

/** Thrown when a request contains invalid data (business rule violation). */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
