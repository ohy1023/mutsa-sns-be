package com.likelionsns.final_project.service;

import com.likelionsns.final_project.domain.dto.CommentDto;
import com.likelionsns.final_project.domain.entity.Comment;
import com.likelionsns.final_project.domain.entity.Post;
import com.likelionsns.final_project.domain.entity.User;
import com.likelionsns.final_project.domain.request.CommentCreateRequest;
import com.likelionsns.final_project.domain.request.CommentUpdateRequest;
import com.likelionsns.final_project.domain.response.CommentUpdateResponse;
import com.likelionsns.final_project.exception.SnsAppException;
import com.likelionsns.final_project.repository.CommentRepository;
import com.likelionsns.final_project.repository.PostRepository;
import com.likelionsns.final_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.likelionsns.final_project.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    public CommentDto createComment(Integer postId, String userName, CommentCreateRequest commentCreateRequest) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new SnsAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage()));

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        Comment savedComment = commentRepository.save(commentCreateRequest.toEntity(user, post));

        return CommentDto.toCommentDto(savedComment);
    }

    public Page<CommentDto> getAllItems(Pageable pageable) {
        Page<Comment> comments = commentRepository.findAll(pageable);
        Page<CommentDto> commentDtos = CommentDto.toDtoList(comments);
        return commentDtos;
    }

    public CommentUpdateResponse updateComment(Integer postId, Integer commentId, CommentUpdateRequest commentUpdateRequest, String userName) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new SnsAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage()));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new SnsAppException(COMMENT_NOT_FOUND, COMMENT_NOT_FOUND.getMessage()));

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        if (isMismatch(userName, comment)) {
            throw new SnsAppException(INVALID_PERMISSION, INVALID_PERMISSION.getMessage());
        }
        comment.setComment(commentUpdateRequest.getComment());
        Comment updateComment = commentRepository.save(comment);
        return CommentUpdateResponse.toResponse(updateComment);
    }

    public boolean deleteComment(Integer postId, Integer commentId, String userName) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new SnsAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage()));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new SnsAppException(COMMENT_NOT_FOUND, COMMENT_NOT_FOUND.getMessage()));

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        if (isMismatch(userName, comment)) {
            throw new SnsAppException(INVALID_PERMISSION, INVALID_PERMISSION.getMessage());
        }
        commentRepository.delete(comment);
        return true;


    }

    private static boolean isMismatch(String userName, Comment comment) {
        return !Objects.equals(comment.getUser().getUserName(), userName);
    }


}
