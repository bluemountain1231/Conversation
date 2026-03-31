package com.interest.circle.service;

import com.interest.circle.dto.CircleDTO;
import com.interest.circle.dto.UserDTO;
import com.interest.circle.entity.*;
import com.interest.circle.repository.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private static final double WEIGHT_LIKE = 1.0;
    private static final double WEIGHT_FAVORITE = 2.0;
    private static final double WEIGHT_COMMENT = 1.5;
    private static final long CACHE_TTL_MS = 5 * 60 * 1000;

    private final PostLikeRepository likeRepository;
    private final PostFavoriteRepository favoriteRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserFollowRepository followRepository;
    private final CircleMemberRepository circleMemberRepository;
    private final CircleRepository circleRepository;
    private final PostService postService;
    private final FollowService followService;
    private final CircleService circleService;

    private final ConcurrentHashMap<String, CacheEntry<?>> cache = new ConcurrentHashMap<>();

    public RecommendationService(PostLikeRepository likeRepository, PostFavoriteRepository favoriteRepository,
                                  CommentRepository commentRepository, PostRepository postRepository,
                                  UserRepository userRepository, UserFollowRepository followRepository,
                                  CircleMemberRepository circleMemberRepository, CircleRepository circleRepository,
                                  PostService postService, FollowService followService, CircleService circleService) {
        this.likeRepository = likeRepository;
        this.favoriteRepository = favoriteRepository;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.circleMemberRepository = circleMemberRepository;
        this.circleRepository = circleRepository;
        this.postService = postService;
        this.followService = followService;
        this.circleService = circleService;
    }

    // ── Post Recommendation (Item-Based CF) ─────────────────

    public Map<String, Object> recommendPosts(Long userId, int page, int size) {
        Map<Long, Map<Long, Double>> matrix = buildUserPostMatrix();
        Map<Long, Double> userInteractions = matrix.getOrDefault(userId, Collections.emptyMap());

        if (userInteractions.isEmpty()) {
            return fallbackHotPosts(page, size, userId);
        }

        Map<Long, Set<Long>> postToUsers = invertMatrix(matrix);
        Map<Long, Double> scores = new HashMap<>();

        for (Map.Entry<Long, Double> interacted : userInteractions.entrySet()) {
            Long postA = interacted.getKey();
            double weightA = interacted.getValue();
            Set<Long> usersA = postToUsers.getOrDefault(postA, Collections.emptySet());

            for (Map.Entry<Long, Set<Long>> candidate : postToUsers.entrySet()) {
                Long postB = candidate.getKey();
                if (userInteractions.containsKey(postB)) continue;

                Set<Long> usersB = candidate.getValue();
                double sim = jaccardSimilarity(usersA, usersB);
                if (sim > 0) {
                    scores.merge(postB, sim * weightA, Double::sum);
                }
            }
        }

        applyTimeDecay(scores);

        List<Long> ranked = scores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return paginatePostIds(ranked, page, size, userId);
    }

    // ── User Recommendation (User-Based CF) ─────────────────

    public List<UserDTO> recommendUsers(Long userId, int limit) {
        Map<Long, Set<Long>> userFeatures = buildUserFeatureVectors();
        Set<Long> myFeatures = userFeatures.getOrDefault(userId, Collections.emptySet());

        if (myFeatures.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> followingIds = followRepository.findFollowingIdsByFollowerId(userId);
        Set<Long> excluded = new HashSet<>(followingIds);
        excluded.add(userId);

        Map<Long, Double> similarities = new HashMap<>();
        for (Map.Entry<Long, Set<Long>> entry : userFeatures.entrySet()) {
            Long candidateId = entry.getKey();
            if (excluded.contains(candidateId)) continue;
            double sim = jaccardSimilarity(myFeatures, entry.getValue());
            if (sim > 0) {
                similarities.put(candidateId, sim);
            }
        }

        List<Long> topSimilarUsers = similarities.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        Set<Long> recommendedIds = new LinkedHashSet<>();
        for (Long simUserId : topSimilarUsers) {
            List<Long> theirFollowing = followRepository.findFollowingIdsByFollowerId(simUserId);
            for (Long fId : theirFollowing) {
                if (!excluded.contains(fId)) {
                    recommendedIds.add(fId);
                }
            }
        }

        if (recommendedIds.isEmpty()) {
            for (Long simUserId : topSimilarUsers) {
                if (!excluded.contains(simUserId)) {
                    recommendedIds.add(simUserId);
                }
            }
        }

        return recommendedIds.stream()
                .limit(limit)
                .map(id -> userRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .map(u -> followService.toUserDTO(u, userId))
                .collect(Collectors.toList());
    }

    // ── Circle Recommendation ───────────────────────────────

    public List<CircleDTO> recommendCircles(Long userId, int limit) {
        Map<Long, Set<Long>> userFeatures = buildUserFeatureVectors();
        Set<Long> myFeatures = userFeatures.getOrDefault(userId, Collections.emptySet());
        List<Long> myCircleIds = circleMemberRepository.findCircleIdsByUserId(userId);
        Set<Long> myCircleSet = new HashSet<>(myCircleIds);

        Map<Long, Double> similarities = new HashMap<>();
        for (Map.Entry<Long, Set<Long>> entry : userFeatures.entrySet()) {
            Long candidateId = entry.getKey();
            if (candidateId.equals(userId)) continue;
            double sim = jaccardSimilarity(myFeatures, entry.getValue());
            if (sim > 0) {
                similarities.put(candidateId, sim);
            }
        }

        List<Long> topSimilarUsers = similarities.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        Map<Long, Double> circleScores = new HashMap<>();
        for (Long simUserId : topSimilarUsers) {
            double sim = similarities.get(simUserId);
            List<Long> theirCircles = circleMemberRepository.findCircleIdsByUserId(simUserId);
            for (Long cId : theirCircles) {
                if (!myCircleSet.contains(cId)) {
                    circleScores.merge(cId, sim, Double::sum);
                }
            }
        }

        if (circleScores.isEmpty()) {
            return circleRepository.findTop10ByOrderByMemberCountDesc().stream()
                    .filter(c -> !myCircleSet.contains(c.getId()))
                    .limit(limit)
                    .map(c -> circleService.toDTO(c, userId))
                    .collect(Collectors.toList());
        }

        return circleScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .map(e -> circleRepository.findById(e.getKey()).orElse(null))
                .filter(Objects::nonNull)
                .map(c -> circleService.toDTO(c, userId))
                .collect(Collectors.toList());
    }

    // ── Internal: Build Matrices ────────────────────────────

    @SuppressWarnings("unchecked")
    private Map<Long, Map<Long, Double>> buildUserPostMatrix() {
        CacheEntry<?> cached = cache.get("userPostMatrix");
        if (cached != null && !cached.isExpired()) {
            return (Map<Long, Map<Long, Double>>) cached.value;
        }

        Map<Long, Map<Long, Double>> matrix = new HashMap<>();

        for (PostLike like : likeRepository.findAll()) {
            matrix.computeIfAbsent(like.getUserId(), k -> new HashMap<>())
                    .merge(like.getPostId(), WEIGHT_LIKE, Double::sum);
        }

        for (PostFavorite fav : favoriteRepository.findAll()) {
            matrix.computeIfAbsent(fav.getUserId(), k -> new HashMap<>())
                    .merge(fav.getPostId(), WEIGHT_FAVORITE, Double::sum);
        }

        for (Comment comment : commentRepository.findAll()) {
            matrix.computeIfAbsent(comment.getUserId(), k -> new HashMap<>())
                    .merge(comment.getPostId(), WEIGHT_COMMENT, Double::sum);
        }

        cache.put("userPostMatrix", new CacheEntry<>(matrix));
        return matrix;
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Set<Long>> buildUserFeatureVectors() {
        CacheEntry<?> cached = cache.get("userFeatures");
        if (cached != null && !cached.isExpired()) {
            return (Map<Long, Set<Long>>) cached.value;
        }

        Map<Long, Set<Long>> features = new HashMap<>();
        long offset = 1_000_000L;

        for (PostLike like : likeRepository.findAll()) {
            features.computeIfAbsent(like.getUserId(), k -> new HashSet<>())
                    .add(like.getPostId());
        }

        for (PostFavorite fav : favoriteRepository.findAll()) {
            features.computeIfAbsent(fav.getUserId(), k -> new HashSet<>())
                    .add(fav.getPostId() + offset);
        }

        for (CircleMember cm : circleMemberRepository.findAll()) {
            features.computeIfAbsent(cm.getUserId(), k -> new HashSet<>())
                    .add(cm.getCircleId() + offset * 2);
        }

        cache.put("userFeatures", new CacheEntry<>(features));
        return features;
    }

    // ── Internal: Utilities ─────────────────────────────────

    private Map<Long, Set<Long>> invertMatrix(Map<Long, Map<Long, Double>> matrix) {
        Map<Long, Set<Long>> postToUsers = new HashMap<>();
        for (Map.Entry<Long, Map<Long, Double>> user : matrix.entrySet()) {
            for (Long postId : user.getValue().keySet()) {
                postToUsers.computeIfAbsent(postId, k -> new HashSet<>()).add(user.getKey());
            }
        }
        return postToUsers;
    }

    private double jaccardSimilarity(Set<Long> a, Set<Long> b) {
        if (a.isEmpty() || b.isEmpty()) return 0.0;
        Set<Long> intersection = new HashSet<>(a);
        intersection.retainAll(b);
        Set<Long> union = new HashSet<>(a);
        union.addAll(b);
        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    private void applyTimeDecay(Map<Long, Double> scores) {
        LocalDateTime now = LocalDateTime.now();
        for (Map.Entry<Long, Double> entry : scores.entrySet()) {
            postRepository.findById(entry.getKey()).ifPresent(post -> {
                long daysSinceCreated = Duration.between(post.getCreatedAt(), now).toDays();
                double decay = 1.0 / (1.0 + daysSinceCreated * 0.05);
                entry.setValue(entry.getValue() * decay);
            });
        }
    }

    private Map<String, Object> fallbackHotPosts(int page, int size, Long userId) {
        List<Post> allPosts = postRepository.findAll();
        allPosts.sort((a, b) -> {
            int scoreA = a.getLikeCount() + a.getFavoriteCount() * 2 + a.getCommentCount();
            int scoreB = b.getLikeCount() + b.getFavoriteCount() * 2 + b.getCommentCount();
            return Integer.compare(scoreB, scoreA);
        });

        int start = page * size;
        int end = Math.min(start + size, allPosts.size());
        List<Post> paged = start < allPosts.size() ? allPosts.subList(start, end) : Collections.emptyList();

        List<com.interest.circle.dto.PostDTO> dtos = paged.stream()
                .map(p -> postService.toPostDTO(p, userId))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("content", dtos);
        result.put("totalElements", allPosts.size());
        result.put("totalPages", (int) Math.ceil((double) allPosts.size() / size));
        result.put("currentPage", page);
        result.put("size", size);
        return result;
    }

    private Map<String, Object> paginatePostIds(List<Long> postIds, int page, int size, Long userId) {
        int start = page * size;
        int end = Math.min(start + size, postIds.size());
        List<Long> pageIds = start < postIds.size() ? postIds.subList(start, end) : Collections.emptyList();

        List<com.interest.circle.dto.PostDTO> dtos = pageIds.stream()
                .map(id -> postRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .map(p -> postService.toPostDTO(p, userId))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("content", dtos);
        result.put("totalElements", postIds.size());
        result.put("totalPages", (int) Math.ceil((double) postIds.size() / size));
        result.put("currentPage", page);
        result.put("size", size);
        return result;
    }

    private static class CacheEntry<T> {
        final T value;
        final long timestamp;

        CacheEntry(T value) {
            this.value = value;
            this.timestamp = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_TTL_MS;
        }
    }
}
