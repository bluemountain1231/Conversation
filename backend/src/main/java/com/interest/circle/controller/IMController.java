package com.interest.circle.controller;

import com.interest.circle.dto.ApiResponse;
import com.interest.circle.entity.User;
import com.interest.circle.repository.UserRepository;
import com.interest.circle.service.TencentIMService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/im")
public class IMController {

    private final TencentIMService imService;
    private final UserRepository userRepository;

    public IMController(TencentIMService imService, UserRepository userRepository) {
        this.imService = imService;
        this.userRepository = userRepository;
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Long) return (Long) principal;
        throw new com.interest.circle.exception.BusinessException(401, "未登录或登录已过期");
    }

    @GetMapping("/usersig")
    public ApiResponse<Map<String, Object>> getUserSig() {
        Long userId = getCurrentUserId();
        String imUserId = imService.getUserImId(userId);
        String userSig = imService.generateUserSig(imUserId);

        Map<String, Object> data = new HashMap<>();
        data.put("sdkAppId", imService.getSdkAppId());
        data.put("userId", imUserId);
        data.put("userSig", userSig);
        return ApiResponse.success(data);
    }

    @GetMapping("/user-info/{userId}")
    public ApiResponse<Map<String, Object>> getUserInfo(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ApiResponse.error(404, "用户不存在");

        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("imUserId", imService.getUserImId(userId));
        data.put("username", user.getUsername());
        data.put("avatar", user.getAvatar());
        return ApiResponse.success(data);
    }
}
