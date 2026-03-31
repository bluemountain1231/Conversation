package com.interest.circle.repository;

import com.interest.circle.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {

    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);

    long countByFollowerId(Long followerId);

    long countByFollowingId(Long followingId);

    @Query("SELECT uf.followingId FROM UserFollow uf WHERE uf.followerId = :userId")
    List<Long> findFollowingIdsByFollowerId(Long userId);

    @Query("SELECT uf.followerId FROM UserFollow uf WHERE uf.followingId = :userId")
    List<Long> findFollowerIdsByFollowingId(Long userId);
}
