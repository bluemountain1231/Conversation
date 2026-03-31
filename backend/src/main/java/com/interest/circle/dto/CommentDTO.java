package com.interest.circle.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDTO {

    private Long id;
    private Long postId;
    private Long userId;
    private Long parentId;
    private String content;
    private LocalDateTime createdAt;
    private String authorName;
    private String authorAvatar;
    private List<CommentDTO> replies;
}
