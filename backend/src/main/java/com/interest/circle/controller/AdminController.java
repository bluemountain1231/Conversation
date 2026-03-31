package com.interest.circle.controller;

import com.interest.circle.dto.ApiResponse;
import com.interest.circle.service.AdminService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    private Long getAdminId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Long)) {
            throw new com.interest.circle.exception.BusinessException(401, "未登录或登录已过期");
        }
        Long userId = (Long) principal;
        adminService.checkAdmin(userId);
        return userId;
    }

    @GetMapping("/users")
    public ApiResponse<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        getAdminId();
        return ApiResponse.success(adminService.getUsers(page, size, keyword));
    }

    @PutMapping("/users/{id}/role")
    public ApiResponse<Void> updateRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Long adminId = getAdminId();
        adminService.updateUserRole(id, body.get("role"), adminId);
        return ApiResponse.success("角色已更新", null);
    }

    @PutMapping("/users/{id}/ban")
    public ApiResponse<Void> toggleBan(@PathVariable Long id) {
        Long adminId = getAdminId();
        adminService.toggleBanUser(id, adminId);
        return ApiResponse.success("操作成功", null);
    }

    @GetMapping("/posts")
    public ApiResponse<Map<String, Object>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        getAdminId();
        return ApiResponse.success(adminService.getPosts(page, size, status));
    }

    @PutMapping("/posts/{id}/status")
    public ApiResponse<Void> updatePostStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Long adminId = getAdminId();
        adminService.updatePostStatus(id, body.get("status"), adminId);
        return ApiResponse.success("帖子状态已更新", null);
    }

    @GetMapping("/circles")
    public ApiResponse<Map<String, Object>> getCircles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        getAdminId();
        return ApiResponse.success(adminService.getCircles(page, size));
    }

    @DeleteMapping("/circles/{id}")
    public ApiResponse<Void> deleteCircle(@PathVariable Long id) {
        Long adminId = getAdminId();
        adminService.deleteCircle(id, adminId);
        return ApiResponse.success("圈子已删除", null);
    }

    @GetMapping("/logs")
    public ApiResponse<Map<String, Object>> getLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        getAdminId();
        return ApiResponse.success(adminService.getLogs(page, size));
    }

    @GetMapping("/export/users")
    public ResponseEntity<byte[]> exportUsers() {
        getAdminId();
        String csv = adminService.exportUsersCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
