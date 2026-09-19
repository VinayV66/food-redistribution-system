package com.foodrescue.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler — catches exceptions thrown by any controller
 * and returns consistent JSON error responses.
 *
 * All error responses follow the same format:
 * {
 *   "timestamp": "2024-01-01T12:00:00",
 *   "status": 400,
 *   "error": "Bad Request",
 *   "message": "...",
 *   "path": "/api/..."
 * }
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Build a standard error response body. */
    private Map<String, Object> buildErrorBody(HttpStatus status, String message, String path) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", path);
        return body;
    }

    /** 404: Resource not found. */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildErrorBody(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI()));
    }

    /** 400: Bad request (business rule violation). */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(
            BadRequestException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest()
                .body(buildErrorBody(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI()));
    }

    /** 403: Unauthorized action. */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(
            UnauthorizedException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildErrorBody(HttpStatus.FORBIDDEN, ex.getMessage(), request.getRequestURI()));
    }

    /** 403: Spring Security access denied. */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildErrorBody(HttpStatus.FORBIDDEN,
                        "Access denied: You don't have permission for this action.",
                        request.getRequestURI()));
    }

    /** 401: Bad login credentials. */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildErrorBody(HttpStatus.UNAUTHORIZED,
                        "Invalid email or password.", request.getRequestURI()));
    }

    /** 400: Donation is expired. */
    @ExceptionHandler(DonationExpiredException.class)
    public ResponseEntity<Map<String, Object>> handleDonationExpired(
            DonationExpiredException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest()
                .body(buildErrorBody(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI()));
    }

    /** 400: Invalid status transition. */
    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidStatus(
            InvalidStatusTransitionException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest()
                .body(buildErrorBody(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI()));
    }

    /**
     * 400: Bean Validation errors (@NotBlank, @Future, etc.).
     * Returns a detailed list of field errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            fieldErrors.put(fieldName, message);
        });

        Map<String, Object> body = buildErrorBody(HttpStatus.BAD_REQUEST,
                "Validation failed", request.getRequestURI());
        body.put("fieldErrors", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    /** 500: Catch-all for unexpected errors. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(
            Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorBody(HttpStatus.INTERNAL_SERVER_ERROR,
                        "An unexpected error occurred. Please try again later.",
                        request.getRequestURI()));
    }
}
