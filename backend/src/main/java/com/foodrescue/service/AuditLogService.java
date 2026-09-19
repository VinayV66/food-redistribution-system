package com.foodrescue.service;

/** Service for recording audit log entries. */
public interface AuditLogService {
    void log(Long userId, String action, String entityType, Long entityId, String description);
}
