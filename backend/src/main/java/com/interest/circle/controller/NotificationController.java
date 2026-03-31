package com.interest.circle.controller;

import com.interest.circle.dto.ApiResponse;
import com.interest.circle.entity.User;
import com.interest.circle.repository.UserRepository;
import com.interest.circle.service.NotificationService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationController(NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Long) return (Long) principal;
        throw new com.interest.circle.exception.BusinessException(401, "未登录或登录已过期");
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(notificationService.getNotifications(userId, page, size));
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount() {
        Long userId = getCurrentUserId();
        return ApiResponse.success(notificationService.getUnreadCount(userId));
    }

    @PostMapping("/message")
    public ApiResponse<Void> sendMessageNotification(@RequestBody Map<String, Object> body) {
        Long fromUserId = getCurrentUserId();
        Long targetUserId = Long.valueOf(body.get("targetUserId").toString());
        String text = (String) body.getOrDefault("text", "发来一条私信");

        User fromUser = userRepository.findById(fromUserId).orElse(null);
        String fromUsername = fromUser != null ? fromUser.getUsername() : "未知用户";

        notificationService.sendNotification(targetUserId, fromUserId, fromUsername,
                "MESSAGE", "给你发了一条私信: " + text, fromUserId);
        return ApiResponse.success("ok", null);
    }

    @PutMapping("/read")
    public ApiResponse<Void> markAllRead() {
        Long userId = getCurrentUserId();
        notificationService.markAllRead(userId);
        return ApiResponse.success("已全部标记为已读", null);
    }
}
