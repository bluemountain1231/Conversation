package com.interest.circle.repository;

import com.interest.circle.entity.CircleMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CircleMemberRepository extends JpaRepository<CircleMember, Long> {

    boolean existsByCircleIdAndUserId(Long circleId, Long userId);

    void deleteByCircleIdAndUserId(Long circleId, Long userId);

    Page<CircleMember> findByCircleIdOrderByJoinedAtDesc(Long circleId, Pageable pageable);

    List<CircleMember> findByUserId(Long userId);

    long countByCircleId(Long circleId);

    @Query("SELECT cm.circleId FROM CircleMember cm WHERE cm.userId = :userId")
    List<Long> findCircleIdsByUserId(Long userId);

    @Query("SELECT DISTINCT cm.userId FROM CircleMember cm WHERE cm.circleId IN :circleIds AND cm.userId != :excludeUserId")
    List<Long> findUserIdsByCircleIdsExcluding(List<Long> circleIds, Long excludeUserId);
}
