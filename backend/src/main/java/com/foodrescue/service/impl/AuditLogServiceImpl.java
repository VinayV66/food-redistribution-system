package com.foodrescue.service.impl;

import com.foodrescue.entity.AuditLog;
import com.foodrescue.repository.AuditLogRepository;
import com.foodrescue.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Records audit log entries so important system actions can be reviewed by admins.
 */
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void log(Long userId, String action, String entityType, Long entityId, String description) {
        AuditLog entry = AuditLog.builder()
                .userId(userId)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .description(description)
                .build();
        auditLogRepository.save(entry);
    }
}
