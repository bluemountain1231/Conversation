package com.interest.circle.controller;

import com.interest.circle.dto.ApiResponse;
import com.interest.circle.dto.CircleDTO;
import com.interest.circle.dto.CircleRequest;
import com.interest.circle.service.CircleService;
import com.interest.circle.service.PostService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/circles")
public class CircleController {

    private final CircleService circleService;
    private final PostService postService;

    public CircleController(CircleService circleService, PostService postService) {
        this.circleService = circleService;
        this.postService = postService;
    }

    @PostMapping
    public ApiResponse<CircleDTO> createCircle(@RequestBody CircleRequest request) {
        return ApiResponse.success(circleService.createCircle(getCurrentUserId(), request));
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> getCircles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(circleService.getCircles(page, size, getCurrentUserIdOrNull()));
    }

    @GetMapping("/hot")
    public ApiResponse<List<CircleDTO>> getHotCircles() {
        return ApiResponse.success(circleService.getHotCircles(getCurrentUserIdOrNull()));
    }

    @GetMapping("/my")
    public ApiResponse<List<CircleDTO>> getMyCircles() {
        return ApiResponse.success(circleService.getMyCircles(getCurrentUserId()));
    }

    @GetMapping("/{id}")
    public ApiResponse<CircleDTO> getCircleById(@PathVariable Long id) {
        return ApiResponse.success(circleService.getCircleById(id, getCurrentUserIdOrNull()));
    }

    @PostMapping("/{id}/join")
    public ApiResponse<CircleDTO> joinCircle(@PathVariable Long id) {
        return ApiResponse.success(circleService.joinCircle(id, getCurrentUserId()));
    }

    @PostMapping("/{id}/leave")
    public ApiResponse<CircleDTO> leaveCircle(@PathVariable Long id) {
        return ApiResponse.success(circleService.leaveCircle(id, getCurrentUserId()));
    }

    @GetMapping("/{id}/members")
    public ApiResponse<Map<String, Object>> getMembers(@PathVariable Long id,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(circleService.getCircleMembers(id, page, size));
    }

    @GetMapping("/{id}/posts")
    public ApiResponse<Map<String, Object>> getCirclePosts(@PathVariable Long id,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(postService.getCirclePosts(id, page, size, getCurrentUserIdOrNull()));
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Long) return (Long) principal;
        throw new com.interest.circle.exception.BusinessException(401, "未登录或登录已过期");
    }

    private Long getCurrentUserIdOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) return (Long) auth.getPrincipal();
        return null;
    }
}
