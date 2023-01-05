package com.likelionsns.final_project.service;


import com.likelionsns.final_project.domain.entity.Like;
import com.likelionsns.final_project.domain.entity.Post;
import com.likelionsns.final_project.domain.entity.User;
import com.likelionsns.final_project.exception.ErrorCode;
import com.likelionsns.final_project.exception.SnsAppException;
import com.likelionsns.final_project.repository.LikeRepository;
import com.likelionsns.final_project.repository.PostRepository;
import com.likelionsns.final_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.likelionsns.final_project.exception.ErrorCode.*;
import static com.likelionsns.final_project.exception.ErrorCode.INVALID_PERMISSION;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;

    public boolean addCount(Integer postId, String userName) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new SnsAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage()));
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        likeRepository.findByPostAndUser(post, user)
                .ifPresent((like) -> {
                    throw new SnsAppException(DUPLICATED_LIKE_COUNT, DUPLICATED_LIKE_COUNT.getMessage());
                });

        likeRepository.save(Like.builder()
                .post(post)
                .user(user)
                .build());
        return true;
    }

    public Long viewCount(Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new SnsAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage()));

        return likeRepository.countByPost(post);
    }

    public List<Like> findAllPost(Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new SnsAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage()));
        return likeRepository.findAllByPost(post);
    }


}
