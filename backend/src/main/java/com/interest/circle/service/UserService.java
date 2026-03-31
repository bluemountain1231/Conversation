package com.interest.circle.service;

import com.interest.circle.dto.UserDTO;
import com.interest.circle.entity.User;
import com.interest.circle.exception.BusinessException;
import com.interest.circle.repository.PostRepository;
import com.interest.circle.repository.UserFollowRepository;
import com.interest.circle.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UserFollowRepository followRepository;

    public UserService(UserRepository userRepository, PostRepository postRepository,
                       UserFollowRepository followRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.followRepository = followRepository;
    }

    public UserDTO getUserById(Long id, Long currentUserId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "User not found"));
        return toUserDTO(user, currentUserId);
    }

    public UserDTO updateUser(Long userId, String username, String bio, String avatar) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "User not found"));

        if (username != null && !username.isBlank()) {
            if (!username.equals(user.getUsername()) && userRepository.existsByUsername(username)) {
                throw new BusinessException(400, "Username already exists");
            }
            user.setUsername(username);
        }
        if (bio != null) user.setBio(bio);
        if (avatar != null && !avatar.isBlank()) user.setAvatar(avatar);
        userRepository.save(user);

        return toUserDTO(user, userId);
    }

    public UserDTO getCurrentUser(Long userId) {
        return getUserById(userId, userId);
    }

    private UserDTO toUserDTO(User user, Long currentUserId) {
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
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        if (currentUserId != null && !currentUserId.equals(user.getId())) {
            dto.setFollowed(followRepository.existsByFollowerIdAndFollowingId(currentUserId, user.getId()));
        }
        return dto;
    }
}
