package com.interest.circle.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private String avatar;
    private String bio;
    private long postCount;
    private long followerCount;
    private long followingCount;
    private boolean followed;
    private String role;
    private LocalDateTime createdAt;
}
