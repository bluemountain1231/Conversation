package com.interest.circle.controller;

import com.interest.circle.dto.ApiResponse;
import com.interest.circle.dto.CommentDTO;
import com.interest.circle.dto.CommentRequest;
import com.interest.circle.service.CommentService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ApiResponse<CommentDTO> addComment(@PathVariable Long postId,
                                              @RequestBody CommentRequest request) {
        Long userId = getCurrentUserId();
        return ApiResponse.success(commentService.addComment(postId, userId, request));
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Long) return (Long) principal;
        throw new com.interest.circle.exception.BusinessException(401, "未登录或登录已过期");
    }

    @GetMapping
    public ApiResponse<List<CommentDTO>> getComments(@PathVariable Long postId) {
        return ApiResponse.success(commentService.getComments(postId));
    }
}
