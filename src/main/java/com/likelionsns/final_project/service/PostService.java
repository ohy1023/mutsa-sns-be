package com.likelionsns.final_project.service;

import com.likelionsns.final_project.domain.dto.PostDto;
import com.likelionsns.final_project.domain.entity.Post;
import com.likelionsns.final_project.domain.entity.User;
import com.likelionsns.final_project.domain.request.PostCreateRequest;
import com.likelionsns.final_project.domain.response.PostDetailResponse;
import com.likelionsns.final_project.exception.SnsAppException;
import com.likelionsns.final_project.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;

import static com.likelionsns.final_project.domain.enums.UserRole.*;
import static com.likelionsns.final_project.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final AlarmRepository alarmRepository;

    @Transactional
    public PostDto createPost(PostCreateRequest postCreateRequest, String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));
        Post savedPost = postRepository.save(postCreateRequest.toEntity(user));
        return PostDto.toPostDto(savedPost);
    }

    public PostDetailResponse findDetail(Integer id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new SnsAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage()));

        Long likeCnt = likeRepository.countByPost(post);

        Long commentCnt = commentRepository.countByPost(post);

        return PostDetailResponse.toResponse(post, likeCnt, commentCnt);
    }

    public Page<PostDto> getAllItems(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return PostDto.toDtoList(posts);
    }

    @Transactional
    public PostDto update(Integer postId, String userName, String title, String body) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new SnsAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage()));

        if (checkAuth(userName, post, user)) {
            throw new SnsAppException(INVALID_PERMISSION, INVALID_PERMISSION.getMessage());
        }

        post.updatePost(title, body);

        return PostDto.toPostDto(post);
    }

    @Transactional
    public boolean delete(String userName, Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new SnsAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage()));
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        log.info("isNotAdmin:{}", !user.getUserRole().equals(ADMIN));
        log.info("isNotMatchName:{}", !userName.equals(post.getUser().getUserName()));

        if (checkAuth(userName, post, user)) {
            throw new SnsAppException(INVALID_PERMISSION, INVALID_PERMISSION.getMessage());
        }
        commentRepository.deleteAllByPost(post);
        likeRepository.deleteAllByPost(post);
        postRepository.delete(post);
        return true;
    }

    private static boolean checkAuth(String userName, Post post, User user) {
        return !user.getUserRole().equals(ADMIN) && !userName.equals(post.getUser().getUserName());
    }


    public Page<PostDto> getMyPost(Pageable pageable, String userName) {

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        Page<Post> posts = postRepository.findAllByUserId(pageable, user.getId());
        Page<PostDto> myPosts = PostDto.toDtoList(posts);
        return myPosts;
    }

}
