package com.interest.circle.service;

import com.interest.circle.dto.CommentDTO;
import com.interest.circle.dto.CommentRequest;
import com.interest.circle.entity.Comment;
import com.interest.circle.entity.Post;
import com.interest.circle.entity.User;
import com.interest.circle.exception.BusinessException;
import com.interest.circle.repository.CommentRepository;
import com.interest.circle.repository.PostRepository;
import com.interest.circle.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CommentDTO addComment(Long postId, Long userId, CommentRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(404, "Post not found"));

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setParentId(request.getParentId());
        commentRepository.save(comment);

        post.setCommentCount((int) commentRepository.countByPostId(postId));
        postRepository.save(post);

        return toCommentDTO(comment);
    }

    public List<CommentDTO> getComments(Long postId) {
        List<Comment> topLevelComments = commentRepository.findByPostIdAndParentIdIsNullOrderByCreatedAtDesc(postId);

        return topLevelComments.stream()
                .map(comment -> {
                    CommentDTO dto = toCommentDTO(comment);
                    List<Comment> replies = commentRepository.findByParentIdOrderByCreatedAtAsc(comment.getId());
                    dto.setReplies(replies.stream()
                            .map(this::toCommentDTO)
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private CommentDTO toCommentDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPostId());
        dto.setUserId(comment.getUserId());
        dto.setParentId(comment.getParentId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setReplies(new ArrayList<>());

        User author = userRepository.findById(comment.getUserId()).orElse(null);
        if (author != null) {
            dto.setAuthorName(author.getUsername());
            dto.setAuthorAvatar(author.getAvatar());
        }

        return dto;
    }
}
