package com.interest.circle.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CircleDTO {
    private Long id;
    private String name;
    private String description;
    private String avatar;
    private Long creatorId;
    private String creatorName;
    private Integer memberCount;
    private Integer postCount;
    private LocalDateTime createdAt;
    private boolean joined;
}
