package com.interest.circle.controller;

import com.interest.circle.dto.ApiResponse;
import com.interest.circle.service.FollowService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/users/{id}/follow")
    public ApiResponse<Map<String, Object>> toggleFollow(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        boolean followed = followService.toggleFollow(userId, id);
        return ApiResponse.success(Map.of("followed", followed));
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Long) return (Long) principal;
        throw new com.interest.circle.exception.BusinessException(401, "未登录或登录已过期");
    }
}
