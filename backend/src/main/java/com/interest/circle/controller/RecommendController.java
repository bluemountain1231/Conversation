package com.interest.circle.controller;

import com.interest.circle.dto.ApiResponse;
import com.interest.circle.dto.CircleDTO;
import com.interest.circle.dto.UserDTO;
import com.interest.circle.service.RecommendationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    private final RecommendationService recommendationService;

    public RecommendController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/posts")
    public ApiResponse<Map<String, Object>> recommendPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(recommendationService.recommendPosts(userId, page, size));
    }

    @GetMapping("/users")
    public ApiResponse<List<UserDTO>> recommendUsers(
            @RequestParam(defaultValue = "5") int limit) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(recommendationService.recommendUsers(userId, limit));
    }

    @GetMapping("/circles")
    public ApiResponse<List<CircleDTO>> recommendCircles(
            @RequestParam(defaultValue = "5") int limit) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(recommendationService.recommendCircles(userId, limit));
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Long) return (Long) principal;
        throw new com.interest.circle.exception.BusinessException(401, "未登录或登录已过期");
    }
}
