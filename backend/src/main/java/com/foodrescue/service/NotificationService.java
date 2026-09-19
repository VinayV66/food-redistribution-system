package com.foodrescue.service;

import com.foodrescue.dto.response.NotificationResponse;
import com.foodrescue.dto.response.PagedResponse;
import com.foodrescue.enums.NotificationType;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    void sendNotification(Long userId, String title, String message, NotificationType type);
    PagedResponse<NotificationResponse> getMyNotifications(Long userId, Pageable pageable);
    long getUnreadCount(Long userId);
    void markAllAsRead(Long userId);
}
