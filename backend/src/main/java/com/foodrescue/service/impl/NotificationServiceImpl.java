package com.foodrescue.service.impl;

import com.foodrescue.dto.response.NotificationResponse;
import com.foodrescue.dto.response.PagedResponse;
import com.foodrescue.entity.Notification;
import com.foodrescue.entity.User;
import com.foodrescue.enums.NotificationType;
import com.foodrescue.exception.ResourceNotFoundException;
import com.foodrescue.repository.NotificationRepository;
import com.foodrescue.repository.UserRepository;
import com.foodrescue.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void sendNotification(Long userId, String title, String message, NotificationType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .read(false)
                .build();

        notificationRepository.save(notification);
        log.debug("Notification sent to user {}: {}", userId, title);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<NotificationResponse> getMyNotifications(Long userId, Pageable pageable) {
        Page<Notification> page = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable);

        Page<NotificationResponse> responsePage = page.map(this::toResponse);
        return PagedResponse.of(responsePage);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadForUser(userId);
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
