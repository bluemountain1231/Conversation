package com.interest.circle.repository;

import com.interest.circle.entity.PostFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PostFavoriteRepository extends JpaRepository<PostFavorite, Long> {

    Optional<PostFavorite> findByPostIdAndUserId(Long postId, Long userId);

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    @Modifying
    @Transactional
    void deleteByPostIdAndUserId(Long postId, Long userId);

    Page<PostFavorite> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
