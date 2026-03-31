package com.interest.circle.service;

import com.interest.circle.dto.LoginRequest;
import com.interest.circle.dto.RegisterRequest;
import com.interest.circle.dto.UserDTO;
import com.interest.circle.entity.User;
import com.interest.circle.exception.BusinessException;
import com.interest.circle.repository.PostRepository;
import com.interest.circle.repository.UserRepository;
import com.interest.circle.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       PostRepository postRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Map<String, Object> register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(400, "Email already exists");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(400, "Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", toUserDTO(user, 0));
        return result;
    }

    public Map<String, Object> login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(400, "User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(400, "Invalid password");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());
        long postCount = postRepository.countByUserId(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", toUserDTO(user, postCount));
        return result;
    }

    private UserDTO toUserDTO(User user, long postCount) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAvatar(user.getAvatar());
        dto.setBio(user.getBio());
        dto.setPostCount(postCount);
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
