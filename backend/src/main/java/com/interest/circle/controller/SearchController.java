package com.interest.circle.controller;

import com.interest.circle.dto.ApiResponse;
import com.interest.circle.service.CircleService;
import com.interest.circle.service.PostService;
import com.interest.circle.service.FollowService;
import com.interest.circle.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final PostService postService;
    private final CircleService circleService;
    private final UserRepository userRepository;
    private final FollowService followService;

    public SearchController(PostService postService, CircleService circleService,
                            UserRepository userRepository, FollowService followService) {
        this.postService = postService;
        this.circleService = circleService;
        this.userRepository = userRepository;
        this.followService = followService;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> search(@RequestParam String keyword,
            @RequestParam(defaultValue = "all") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long currentUserId = getCurrentUserIdOrNull();
        Map<String, Object> result = new HashMap<>();

        if ("all".equals(type) || "post".equals(type)) {
            result.put("posts", postService.searchPosts(keyword, page, size, currentUserId));
        }
        if ("all".equals(type) || "circle".equals(type)) {
            result.put("circles", circleService.searchCircles(keyword, page, size, currentUserId));
        }
        if ("all".equals(type) || "user".equals(type)) {
            var userPage = userRepository.searchByUsername(keyword, PageRequest.of(page, size));
            var users = userPage.getContent().stream()
                    .map(u -> followService.toUserDTO(u, currentUserId))
                    .collect(Collectors.toList());
            Map<String, Object> userResult = new HashMap<>();
            userResult.put("content", users);
            userResult.put("totalElements", userPage.getTotalElements());
            result.put("users", userResult);
        }

        return ApiResponse.success(result);
    }

    private Long getCurrentUserIdOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) return (Long) auth.getPrincipal();
        return null;
    }
}
