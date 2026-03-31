package com.interest.circle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private Long circleId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String images;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer likeCount = 0;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer commentCount = 0;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer favoriteCount = 0;

    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'APPROVED'")
    private String status = "APPROVED";

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
