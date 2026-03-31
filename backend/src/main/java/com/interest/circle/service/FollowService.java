package com.interest.circle.service;

import com.interest.circle.dto.UserDTO;
import com.interest.circle.entity.User;
import com.interest.circle.exception.BusinessException;
import com.interest.circle.repository.CircleMemberRepository;
import com.interest.circle.repository.PostRepository;
import com.interest.circle.repository.UserFollowRepository;
import com.interest.circle.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowService {

    private final UserFollowRepository followRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CircleMemberRepository circleMemberRepository;
    private final NotificationService notificationService;

    public FollowService(UserFollowRepository followRepository, UserRepository userRepository,
                         PostRepository postRepository, CircleMemberRepository circleMemberRepository,
                         NotificationService notificationService) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.circleMemberRepository = circleMemberRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public boolean toggleFollow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new BusinessException(400, "不能关注自己");
        }
        userRepository.findById(followingId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        boolean exists = followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
        if (exists) {
            followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
            return false;
        } else {
            var follow = new com.interest.circle.entity.UserFollow();
            follow.setFollowerId(followerId);
            follow.setFollowingId(followingId);
            followRepository.save(follow);
            User follower = userRepository.findById(followerId).orElse(null);
            if (follower != null) {
                notificationService.sendNotification(followingId, followerId,
                        follower.getUsername(), "FOLLOW", "关注了你", followerId);
            }
            return true;
        }
    }

    public UserDTO toUserDTO(User user, Long currentUserId) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAvatar(user.getAvatar());
        dto.setBio(user.getBio());
        dto.setPostCount(postRepository.countByUserId(user.getId()));
        dto.setFollowerCount(followRepository.countByFollowingId(user.getId()));
        dto.setFollowingCount(followRepository.countByFollowerId(user.getId()));
        dto.setCreatedAt(user.getCreatedAt());
        if (currentUserId != null && !currentUserId.equals(user.getId())) {
            dto.setFollowed(followRepository.existsByFollowerIdAndFollowingId(currentUserId, user.getId()));
        }
        return dto;
    }

    public List<UserDTO> getRecommendedUsers(Long userId, int limit) {
        List<Long> myCircleIds = circleMemberRepository.findCircleIdsByUserId(userId);
        if (myCircleIds.isEmpty()) return Collections.emptyList();

        List<Long> candidateIds = circleMemberRepository.findUserIdsByCircleIdsExcluding(myCircleIds, userId);
        List<Long> followingIds = followRepository.findFollowingIdsByFollowerId(userId);
        candidateIds.removeAll(followingIds);

        return candidateIds.stream()
                .distinct()
                .limit(limit)
                .map(id -> userRepository.findById(id).orElse(null))
                .filter(u -> u != null)
                .map(u -> toUserDTO(u, userId))
                .collect(Collectors.toList());
    }
}
