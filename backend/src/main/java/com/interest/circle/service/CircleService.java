package com.interest.circle.service;

import com.interest.circle.dto.CircleDTO;
import com.interest.circle.dto.CircleRequest;
import com.interest.circle.entity.Circle;
import com.interest.circle.entity.CircleMember;
import com.interest.circle.entity.User;
import com.interest.circle.exception.BusinessException;
import com.interest.circle.repository.CircleMemberRepository;
import com.interest.circle.repository.CircleRepository;
import com.interest.circle.repository.PostRepository;
import com.interest.circle.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CircleService {

    private final CircleRepository circleRepository;
    private final CircleMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CircleService(CircleRepository circleRepository, CircleMemberRepository memberRepository,
                         UserRepository userRepository, PostRepository postRepository) {
        this.circleRepository = circleRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public CircleDTO createCircle(Long userId, CircleRequest request) {
        Circle circle = new Circle();
        circle.setName(request.getName());
        circle.setDescription(request.getDescription());
        circle.setAvatar(request.getAvatar());
        circle.setCreatorId(userId);
        circle.setMemberCount(1);
        circle.setPostCount(0);
        circleRepository.save(circle);

        CircleMember member = new CircleMember();
        member.setCircleId(circle.getId());
        member.setUserId(userId);
        memberRepository.save(member);

        return toDTO(circle, userId);
    }

    public CircleDTO getCircleById(Long id, Long currentUserId) {
        Circle circle = circleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "圈子不存在"));
        CircleDTO dto = toDTO(circle, currentUserId);
        dto.setPostCount((int) postRepository.countByCircleId(id));
        return dto;
    }

    public Map<String, Object> getCircles(int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Circle> circlePage = circleRepository.findAllByOrderByMemberCountDesc(pageable);
        return buildPageResult(circlePage, currentUserId);
    }

    public List<CircleDTO> getHotCircles(Long currentUserId) {
        return circleRepository.findTop10ByOrderByMemberCountDesc().stream()
                .map(c -> toDTO(c, currentUserId))
                .collect(Collectors.toList());
    }

    public List<CircleDTO> getMyCircles(Long userId) {
        List<Long> circleIds = memberRepository.findCircleIdsByUserId(userId);
        return circleIds.stream()
                .map(id -> circleRepository.findById(id).orElse(null))
                .filter(c -> c != null)
                .map(c -> toDTO(c, userId))
                .collect(Collectors.toList());
    }

    @Transactional
    public CircleDTO joinCircle(Long circleId, Long userId) {
        Circle circle = circleRepository.findById(circleId)
                .orElseThrow(() -> new BusinessException(404, "圈子不存在"));

        if (memberRepository.existsByCircleIdAndUserId(circleId, userId)) {
            throw new BusinessException(400, "已经加入该圈子");
        }

        CircleMember member = new CircleMember();
        member.setCircleId(circleId);
        member.setUserId(userId);
        memberRepository.save(member);

        circle.setMemberCount((int) memberRepository.countByCircleId(circleId));
        circleRepository.save(circle);

        return toDTO(circle, userId);
    }

    @Transactional
    public CircleDTO leaveCircle(Long circleId, Long userId) {
        Circle circle = circleRepository.findById(circleId)
                .orElseThrow(() -> new BusinessException(404, "圈子不存在"));

        if (circle.getCreatorId().equals(userId)) {
            throw new BusinessException(400, "圈主不能退出圈子");
        }

        if (!memberRepository.existsByCircleIdAndUserId(circleId, userId)) {
            throw new BusinessException(400, "未加入该圈子");
        }

        memberRepository.deleteByCircleIdAndUserId(circleId, userId);
        circle.setMemberCount((int) memberRepository.countByCircleId(circleId));
        circleRepository.save(circle);

        return toDTO(circle, userId);
    }

    public Map<String, Object> getCircleMembers(Long circleId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CircleMember> memberPage = memberRepository.findByCircleIdOrderByJoinedAtDesc(circleId, pageable);

        List<Map<String, Object>> members = memberPage.getContent().stream().map(m -> {
            User user = userRepository.findById(m.getUserId()).orElse(null);
            Map<String, Object> map = new HashMap<>();
            if (user != null) {
                map.put("id", user.getId());
                map.put("username", user.getUsername());
                map.put("avatar", user.getAvatar());
                map.put("joinedAt", m.getJoinedAt());
            }
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("content", members);
        result.put("totalElements", memberPage.getTotalElements());
        return result;
    }

    public Map<String, Object> searchCircles(String keyword, int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Circle> circlePage = circleRepository.searchByKeyword(keyword, pageable);
        return buildPageResult(circlePage, currentUserId);
    }

    private Map<String, Object> buildPageResult(Page<Circle> circlePage, Long currentUserId) {
        List<CircleDTO> dtos = circlePage.getContent().stream()
                .map(c -> toDTO(c, currentUserId))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("content", dtos);
        result.put("totalElements", circlePage.getTotalElements());
        result.put("totalPages", circlePage.getTotalPages());
        result.put("currentPage", circlePage.getNumber());
        return result;
    }

    public CircleDTO toDTO(Circle circle, Long currentUserId) {
        CircleDTO dto = new CircleDTO();
        dto.setId(circle.getId());
        dto.setName(circle.getName());
        dto.setDescription(circle.getDescription());
        dto.setAvatar(circle.getAvatar());
        dto.setCreatorId(circle.getCreatorId());
        dto.setMemberCount(circle.getMemberCount());
        dto.setPostCount(circle.getPostCount());
        dto.setCreatedAt(circle.getCreatedAt());

        User creator = userRepository.findById(circle.getCreatorId()).orElse(null);
        if (creator != null) dto.setCreatorName(creator.getUsername());

        if (currentUserId != null) {
            dto.setJoined(memberRepository.existsByCircleIdAndUserId(circle.getId(), currentUserId));
        }
        return dto;
    }
}
