package com.interest.circle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admin_logs")
public class AdminLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long adminId;

    @Column(nullable = false)
    private String adminName;

    @Column(nullable = false)
    private String action;

    @Column(columnDefinition = "TEXT")
    private String detail;

    private String targetType;

    private Long targetId;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
