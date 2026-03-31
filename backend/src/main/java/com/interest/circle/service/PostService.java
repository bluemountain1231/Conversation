package com.interest.circle.service;

import com.interest.circle.dto.PostDTO;
import com.interest.circle.dto.PostRequest;
import com.interest.circle.entity.Post;
import com.interest.circle.entity.PostFavorite;
import com.interest.circle.entity.PostLike;
import com.interest.circle.entity.User;
import com.interest.circle.exception.BusinessException;
import com.interest.circle.repository.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostFavoriteRepository postFavoriteRepository;
    private final CircleRepository circleRepository;
    private final UserFollowRepository userFollowRepository;
    private final NotificationService notificationService;
    private final RecommendationService recommendationService;

    public PostService(PostRepository postRepository, UserRepository userRepository,
                       PostLikeRepository postLikeRepository, PostFavoriteRepository postFavoriteRepository,
                       CircleRepository circleRepository, UserFollowRepository userFollowRepository,
                       NotificationService notificationService,
                       @Lazy RecommendationService recommendationService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postLikeRepository = postLikeRepository;
        this.postFavoriteRepository = postFavoriteRepository;
        this.circleRepository = circleRepository;
        this.userFollowRepository = userFollowRepository;
        this.notificationService = notificationService;
        this.recommendationService = recommendationService;
    }

    public PostDTO createPost(Long userId, PostRequest request) {
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCircleId(request.getCircleId());
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            post.setImages(String.join(",", request.getImages()));
        }
        postRepository.save(post);

        if (request.getCircleId() != null) {
            circleRepository.findById(request.getCircleId()).ifPresent(circle -> {
                circle.setPostCount((int) postRepository.countByCircleId(circle.getId()));
                circleRepository.save(circle);
            });
        }

        return toPostDTO(post, userId);
    }

    public PostDTO getPostById(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(404, "Post not found"));
        return toPostDTO(post, currentUserId);
    }

    public Map<String, Object> getPosts(int page, int size, Long currentUserId) {
        if (currentUserId != null) {
            try {
                Map<String, Object> cfResult = recommendationService.recommendPosts(currentUserId, page, size);
                @SuppressWarnings("unchecked")
                List<?> content = (List<?>) cfResult.get("content");
                if (content != null && !content.isEmpty()) {
                    return cfResult;
                }
            } catch (Exception ignored) {}
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        return buildPageResult(postPage, currentUserId);
    }

    public Map<String, Object> getUserPosts(Long userId, int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return buildPageResult(postPage, currentUserId);
    }

    public Map<String, Object> getCirclePosts(Long circleId, int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findByCircleIdOrderByCreatedAtDesc(circleId, pageable);
        return buildPageResult(postPage, currentUserId);
    }

    public Map<String, Object> getFeed(Long userId, int page, int size) {
        List<Long> followingIds = userFollowRepository.findFollowingIdsByFollowerId(userId);
        followingIds.add(userId);
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findByUserIdInOrderByCreatedAtDesc(followingIds, pageable);
        return buildPageResult(postPage, userId);
    }

    public Map<String, Object> searchPosts(String keyword, int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.searchByKeyword(keyword, pageable);
        return buildPageResult(postPage, currentUserId);
    }

    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(404, "Post not found"));
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException(403, "You can only delete your own posts");
        }
        postRepository.delete(post);
    }

    @Transactional
    public PostDTO toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(404, "Post not found"));

        boolean exists = postLikeRepository.existsByPostIdAndUserId(postId, userId);
        if (exists) {
            postLikeRepository.deleteByPostIdAndUserId(postId, userId);
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
        } else {
            PostLike like = new PostLike();
            like.setPostId(postId);
            like.setUserId(userId);
            postLikeRepository.save(like);
            post.setLikeCount(post.getLikeCount() + 1);
            User liker = userRepository.findById(userId).orElse(null);
            if (liker != null) {
                notificationService.sendNotification(post.getUserId(), userId,
                        liker.getUsername(), "LIKE", "赞了你的帖子「" + post.getTitle() + "」", postId);
            }
        }
        postRepository.save(post);
        return toPostDTO(post, userId);
    }

    @Transactional
    public PostDTO toggleFavorite(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(404, "Post not found"));

        boolean exists = postFavoriteRepository.existsByPostIdAndUserId(postId, userId);
        if (exists) {
            postFavoriteRepository.deleteByPostIdAndUserId(postId, userId);
            post.setFavoriteCount(Math.max(0, post.getFavoriteCount() - 1));
        } else {
            PostFavorite favorite = new PostFavorite();
            favorite.setPostId(postId);
            favorite.setUserId(userId);
            postFavoriteRepository.save(favorite);
            post.setFavoriteCount(post.getFavoriteCount() + 1);
        }
        postRepository.save(post);
        return toPostDTO(post, userId);
    }

    public Map<String, Object> getUserFavorites(Long userId, int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostFavorite> favoritePage = postFavoriteRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        List<PostDTO> postDTOs = favoritePage.getContent().stream()
                .map(fav -> {
                    Post post = postRepository.findById(fav.getPostId()).orElse(null);
                    if (post == null) return null;
                    return toPostDTO(post, currentUserId);
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("content", postDTOs);
        result.put("totalElements", favoritePage.getTotalElements());
        result.put("totalPages", favoritePage.getTotalPages());
        result.put("currentPage", favoritePage.getNumber());
        result.put("size", favoritePage.getSize());
        return result;
    }

    private Map<String, Object> buildPageResult(Page<Post> postPage, Long currentUserId) {
        List<PostDTO> postDTOs = postPage.getContent().stream()
                .map(post -> toPostDTO(post, currentUserId))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("content", postDTOs);
        result.put("totalElements", postPage.getTotalElements());
        result.put("totalPages", postPage.getTotalPages());
        result.put("currentPage", postPage.getNumber());
        result.put("size", postPage.getSize());
        return result;
    }

    public PostDTO toPostDTO(Post post, Long currentUserId) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setUserId(post.getUserId());
        dto.setCircleId(post.getCircleId());
        if (post.getCircleId() != null) {
            circleRepository.findById(post.getCircleId()).ifPresent(c -> dto.setCircleName(c.getName()));
        }
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setImages(post.getImages());
        dto.setLikeCount(post.getLikeCount());
        dto.setCommentCount(post.getCommentCount());
        dto.setFavoriteCount(post.getFavoriteCount());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());

        User author = userRepository.findById(post.getUserId()).orElse(null);
        if (author != null) {
            dto.setAuthorName(author.getUsername());
            dto.setAuthorAvatar(author.getAvatar());
        }

        if (currentUserId != null) {
            dto.setLiked(postLikeRepository.existsByPostIdAndUserId(post.getId(), currentUserId));
            dto.setFavorited(postFavoriteRepository.existsByPostIdAndUserId(post.getId(), currentUserId));
        }

        return dto;
    }
}
