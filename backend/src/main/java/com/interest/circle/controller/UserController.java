package com.interest.circle.controller;

import com.interest.circle.dto.ApiResponse;
import com.interest.circle.dto.UserDTO;
import com.interest.circle.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ApiResponse<UserDTO> getCurrentUser() {
        Long userId = getCurrentUserId();
        return ApiResponse.success(userService.getCurrentUser(userId));
    }

    @PutMapping("/me")
    public ApiResponse<UserDTO> updateUser(@RequestBody Map<String, String> request) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(userService.updateUser(
                userId, request.get("username"), request.get("bio"), request.get("avatar")));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserDTO> getUserById(@PathVariable Long id) {
        Long currentUserId = getCurrentUserIdOrNull();
        return ApiResponse.success(userService.getUserById(id, currentUserId));
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
