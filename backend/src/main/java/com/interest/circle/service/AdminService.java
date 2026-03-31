package com.interest.circle.service;

import com.interest.circle.entity.*;
import com.interest.circle.exception.BusinessException;
import com.interest.circle.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CircleRepository circleRepository;
    private final AdminLogRepository adminLogRepository;

    public AdminService(UserRepository userRepository, PostRepository postRepository,
                        CircleRepository circleRepository, AdminLogRepository adminLogRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.circleRepository = circleRepository;
        this.adminLogRepository = adminLogRepository;
    }

    public void checkAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(403, "无权限"));
        if (!"ADMIN".equals(user.getRole())) {
            throw new BusinessException(403, "需要管理员权限");
        }
    }

    public Map<String, Object> getUsers(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> userPage;
        if (keyword != null && !keyword.isBlank()) {
            userPage = userRepository.searchByUsername(keyword, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("content", userPage.getContent().stream().map(this::toUserMap).toList());
        result.put("totalElements", userPage.getTotalElements());
        result.put("totalPages", userPage.getTotalPages());
        return result;
    }

    public Map<String, Object> getPosts(int page, int size, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> postPage = postRepository.findAll(pageable);
        Map<String, Object> result = new HashMap<>();
        result.put("content", postPage.getContent());
        result.put("totalElements", postPage.getTotalElements());
        result.put("totalPages", postPage.getTotalPages());
        return result;
    }

    public Map<String, Object> getCircles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Circle> circlePage = circleRepository.findAll(pageable);
        Map<String, Object> result = new HashMap<>();
        result.put("content", circlePage.getContent());
        result.put("totalElements", circlePage.getTotalElements());
        result.put("totalPages", circlePage.getTotalPages());
        return result;
    }

    public void updateUserRole(Long targetUserId, String role, Long adminId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        user.setRole(role);
        userRepository.save(user);
        log(adminId, "修改角色", "USER", targetUserId, "设为 " + role);
    }

    public void toggleBanUser(Long targetUserId, Long adminId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        user.setBanned(!user.getBanned());
        userRepository.save(user);
        log(adminId, user.getBanned() ? "封禁用户" : "解封用户", "USER", targetUserId, "");
    }

    public void updatePostStatus(Long postId, String status, Long adminId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(404, "帖子不存在"));
        post.setStatus(status);
        postRepository.save(post);
        log(adminId, "审核帖子", "POST", postId, "状态: " + status);
    }

    public void deleteCircle(Long circleId, Long adminId) {
        circleRepository.deleteById(circleId);
        log(adminId, "删除圈子", "CIRCLE", circleId, "");
    }

    public Map<String, Object> getLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminLog> logPage = adminLogRepository.findAllByOrderByCreatedAtDesc(pageable);
        Map<String, Object> result = new HashMap<>();
        result.put("content", logPage.getContent());
        result.put("totalElements", logPage.getTotalElements());
        result.put("totalPages", logPage.getTotalPages());
        return result;
    }

    public String exportUsersCsv() {
        var users = userRepository.findAll();
        StringBuilder sb = new StringBuilder("ID,用户名,邮箱,角色,状态,注册时间\n");
        for (User u : users) {
            sb.append(u.getId()).append(",")
              .append(u.getUsername()).append(",")
              .append(u.getEmail()).append(",")
              .append(u.getRole()).append(",")
              .append(u.getBanned() ? "封禁" : "正常").append(",")
              .append(u.getCreatedAt()).append("\n");
        }
        return sb.toString();
    }

    private void log(Long adminId, String action, String targetType, Long targetId, String detail) {
        User admin = userRepository.findById(adminId).orElse(null);
        AdminLog log = new AdminLog();
        log.setAdminId(adminId);
        log.setAdminName(admin != null ? admin.getUsername() : "unknown");
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetail(detail);
        adminLogRepository.save(log);
    }

    private Map<String, Object> toUserMap(User u) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", u.getId());
        map.put("username", u.getUsername());
        map.put("email", u.getEmail());
        map.put("avatar", u.getAvatar());
        map.put("role", u.getRole());
        map.put("banned", u.getBanned());
        map.put("createdAt", u.getCreatedAt());
        return map;
    }
}
