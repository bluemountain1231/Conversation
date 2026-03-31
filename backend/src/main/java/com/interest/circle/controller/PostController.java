package com.interest.circle.controller;

import com.interest.circle.dto.ApiResponse;
import com.interest.circle.dto.PostDTO;
import com.interest.circle.dto.PostRequest;
import com.interest.circle.service.PostService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ApiResponse<PostDTO> createPost(@RequestBody PostRequest request) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(postService.createPost(userId, request));
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long currentUserId = getCurrentUserIdOrNull();
        return ApiResponse.success(postService.getPosts(page, size, currentUserId));
    }

    @GetMapping("/{id}")
    public ApiResponse<PostDTO> getPostById(@PathVariable Long id) {
        Long currentUserId = getCurrentUserIdOrNull();
        return ApiResponse.success(postService.getPostById(id, currentUserId));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePost(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        postService.deletePost(id, userId);
        return ApiResponse.success("Post deleted", null);
    }

    @PostMapping("/{id}/like")
    public ApiResponse<PostDTO> toggleLike(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(postService.toggleLike(id, userId));
    }

    @PostMapping("/{id}/favorite")
    public ApiResponse<PostDTO> toggleFavorite(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(postService.toggleFavorite(id, userId));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<Map<String, Object>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long currentUserId = getCurrentUserIdOrNull();
        return ApiResponse.success(postService.getUserPosts(userId, page, size, currentUserId));
    }

    @GetMapping("/feed")
    public ApiResponse<Map<String, Object>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(postService.getFeed(userId, page, size));
    }

    @GetMapping("/favorites")
    public ApiResponse<Map<String, Object>> getUserFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(postService.getUserFavorites(userId, page, size, userId));
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Long) return (Long) principal;
        throw new com.interest.circle.exception.BusinessException(401, "未登录或登录已过期");
    }

    private Long getCurrentUserIdOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) {
            return (Long) auth.getPrincipal();
        }
        return null;
    }
}
