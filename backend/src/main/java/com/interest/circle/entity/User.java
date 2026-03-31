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
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;

    @Column(nullable = false)
    private String password;

    @Column(columnDefinition = "VARCHAR(500) DEFAULT 'https://api.dicebear.com/7.x/avataaars/svg?seed=default'")
    private String avatar = "https://api.dicebear.com/7.x/avataaars/svg?seed=default";

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'USER'")
    private String role = "USER";

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean banned = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
