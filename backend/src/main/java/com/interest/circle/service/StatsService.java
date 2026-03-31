package com.interest.circle.service;

import com.interest.circle.repository.*;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.time.LocalDate;
import java.util.*;

@Service
public class StatsService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CircleRepository circleRepository;
    private final EntityManager entityManager;

    public StatsService(UserRepository userRepository, PostRepository postRepository,
                        CircleRepository circleRepository, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.circleRepository = circleRepository;
        this.entityManager = entityManager;
    }

    public Map<String, Object> getUserStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("postCount", postRepository.countByUserId(userId));
        stats.put("postTrend", getDailyTrend("posts", "user_id = " + userId, 30));
        stats.put("likeTrend", getInteractionTrend("post_likes", userId, 30));
        stats.put("commentTrend", getInteractionTrend("comments", userId, 30));
        return stats;
    }

    public Map<String, Object> getAdminOverview() {
        Map<String, Object> overview = new HashMap<>();
        overview.put("totalUsers", userRepository.count());
        overview.put("totalPosts", postRepository.count());
        overview.put("totalCircles", circleRepository.count());
        overview.put("todayPosts", countToday("posts"));
        overview.put("todayUsers", countToday("users"));
        return overview;
    }

    public Map<String, Object> getAdminTrends() {
        Map<String, Object> trends = new HashMap<>();
        trends.put("userTrend", getDailyTrend("users", null, 30));
        trends.put("postTrend", getDailyTrend("posts", null, 30));
        return trends;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getDailyTrend(String table, String condition, int days) {
        String where = condition != null ? " AND " + condition : "";
        String sql = "SELECT DATE(created_at) as date, COUNT(*) as count FROM " + table +
                " WHERE created_at >= :startDate" + where +
                " GROUP BY DATE(created_at) ORDER BY date";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("startDate", LocalDate.now().minusDays(days).toString());
        List<Object[]> rows = query.getResultList();

        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", row[0].toString());
            item.put("count", ((Number) row[1]).longValue());
            result.add(item);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getInteractionTrend(String table, Long userId, int days) {
        String userCol = "comments".equals(table) ? "user_id" : "user_id";
        String sql = "SELECT DATE(pl.created_at) as date, COUNT(*) as count FROM " + table + " pl " +
                "JOIN posts p ON pl.post_id = p.id WHERE p.user_id = :userId AND pl.created_at >= :startDate " +
                "GROUP BY DATE(pl.created_at) ORDER BY date";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("userId", userId);
        query.setParameter("startDate", LocalDate.now().minusDays(days).toString());
        List<Object[]> rows = query.getResultList();

        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", row[0].toString());
            item.put("count", ((Number) row[1]).longValue());
            result.add(item);
        }
        return result;
    }

    private long countToday(String table) {
        String sql = "SELECT COUNT(*) FROM " + table + " WHERE DATE(created_at) = CURDATE()";
        return ((Number) entityManager.createNativeQuery(sql).getSingleResult()).longValue();
    }
}
