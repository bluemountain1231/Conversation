package com.interest.circle.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDTO {

    private Long id;
    private Long userId;
    private Long circleId;
    private String circleName;
    private String title;
    private String content;
    private String images;
    private Integer likeCount;
    private Integer commentCount;
    private Integer favoriteCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String authorName;
    private String authorAvatar;
    private boolean liked;
    private boolean favorited;
}
