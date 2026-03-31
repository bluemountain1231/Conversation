package com.interest.circle.repository;

import com.interest.circle.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByTargetUserIdOrderByCreatedAtDesc(Long targetUserId, Pageable pageable);
    long countByTargetUserIdAndIsReadFalse(Long targetUserId);
    void deleteByTargetUserId(Long targetUserId);
}
