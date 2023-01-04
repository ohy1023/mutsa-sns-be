package com.likelionsns.final_project.service;

import com.likelionsns.final_project.domain.dto.PostDto;
import com.likelionsns.final_project.domain.entity.Post;
import com.likelionsns.final_project.domain.entity.User;
import com.likelionsns.final_project.domain.request.PostCreateRequest;
import com.likelionsns.final_project.exception.SnsAppException;
import com.likelionsns.final_project.repository.PostRepository;
import com.likelionsns.final_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.Objects;

import static com.likelionsns.final_project.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public PostDto createPost(PostCreateRequest postCreateRequest, String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));
        Post savedPost = postRepository.save(postCreateRequest.toEntity(user));
        return PostDto.toPostDto(savedPost);
    }

    public PostDto findDetail(Integer id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new SnsAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage()));
        return PostDto.toPostDto(post);
    }

    public Page<PostDto> getAllItems(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        Page<PostDto> postDtos = PostDto.toDtoList(posts);
        return postDtos;
    }

    public PostDto update(Integer postId, String userName, String title, String body) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new SnsAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage()));

        if (isMismatch(userName, post)) {
            throw new SnsAppException(INVALID_PERMISSION, INVALID_PERMISSION.getMessage());
        }

        post.setTitle(title);
        post.setBody(body);
        Post updatedPost = postRepository.save(post);
        return PostDto.toPostDto(updatedPost);
    }

    public boolean delete(String userName, Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new SnsAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage()));
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        if (isMismatch(userName, post)) {
            throw new SnsAppException(INVALID_PERMISSION, INVALID_PERMISSION.getMessage());
        }
        postRepository.delete(post);
        return true;
    }

    public Page<PostDto> getMyPost(Pageable pageable, String userName) {

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        Page<Post> posts = postRepository.findAllByUserId(pageable,user.getId());
        Page<PostDto> myPosts = PostDto.toDtoList(posts);
        return myPosts;
    }
    private static boolean isMismatch(String userName, Post post) {
        return !Objects.equals(post.getUser().getUserName(), userName);
    }


}
