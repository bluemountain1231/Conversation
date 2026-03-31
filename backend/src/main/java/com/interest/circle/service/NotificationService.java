package com.interest.circle.service;

import com.interest.circle.entity.Notification;
import com.interest.circle.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepository notificationRepository,
                               SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotification(Long targetUserId, Long fromUserId, String fromUsername,
                                  String type, String content, Long relatedId) {
        if (targetUserId.equals(fromUserId)) return;

        Notification n = new Notification();
        n.setTargetUserId(targetUserId);
        n.setFromUserId(fromUserId);
        n.setFromUsername(fromUsername);
        n.setType(type);
        n.setContent(content);
        n.setRelatedId(relatedId);
        notificationRepository.save(n);

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", n.getId());
        payload.put("type", type);
        payload.put("content", content);
        payload.put("fromUsername", fromUsername);
        payload.put("fromUserId", fromUserId);
        payload.put("relatedId", relatedId);
        payload.put("createdAt", n.getCreatedAt());

        messagingTemplate.convertAndSend("/topic/notifications/" + targetUserId, payload);
    }

    public Map<String, Object> getNotifications(Long userId, int page, int size) {
        Page<Notification> p = notificationRepository.findByTargetUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));
        Map<String, Object> result = new HashMap<>();
        result.put("content", p.getContent());
        result.put("totalElements", p.getTotalElements());
        result.put("totalPages", p.getTotalPages());
        return result;
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByTargetUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAllRead(Long userId) {
        Page<Notification> page = notificationRepository.findByTargetUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, 1000));
        page.getContent().forEach(n -> {
            if (!n.getIsRead()) {
                n.setIsRead(true);
                notificationRepository.save(n);
            }
        });
    }
}
