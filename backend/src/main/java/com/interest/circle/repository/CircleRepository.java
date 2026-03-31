package com.interest.circle.repository;

import com.interest.circle.entity.Circle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CircleRepository extends JpaRepository<Circle, Long> {

    Page<Circle> findAllByOrderByMemberCountDesc(Pageable pageable);

    Page<Circle> findByCreatorIdOrderByCreatedAtDesc(Long creatorId, Pageable pageable);

    @Query("SELECT c FROM Circle c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Circle> searchByKeyword(String keyword, Pageable pageable);

    List<Circle> findTop10ByOrderByMemberCountDesc();
}
