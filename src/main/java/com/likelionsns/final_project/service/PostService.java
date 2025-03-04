package com.likelionsns.final_project.service;

import com.likelionsns.final_project.domain.constant.FilterConstants;
import com.likelionsns.final_project.domain.dto.PostDto;
import com.likelionsns.final_project.domain.entity.Post;
import com.likelionsns.final_project.domain.entity.User;
import com.likelionsns.final_project.domain.request.PostCreateRequest;
import com.likelionsns.final_project.domain.response.PostDetailResponse;
import com.likelionsns.final_project.exception.SnsAppException;
import com.likelionsns.final_project.repository.*;
import com.likelionsns.final_project.utils.FilterManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.likelionsns.final_project.domain.enums.UserRole.*;
import static com.likelionsns.final_project.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final FilterManager filterManager;

    public PostDto createPost(PostCreateRequest postCreateRequest, String userName) {
        // 사용자 정보 가져오기
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        // 게시물 생성 및 저장
        Post savedPost = postRepository.save(postCreateRequest.toEntity(user));
        return PostDto.toPostDto(savedPost);
    }

    @Transactional(readOnly = true)
    public PostDetailResponse findDetail(Integer id) {
        // 게시물 상세 정보 조회
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new SnsAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage()));

        // 좋아요 개수 및 댓글 개수 조회
        Long likeCnt = likeRepository.countByPost(post);
        Long commentCnt = commentRepository.countByPost(post);

        return PostDetailResponse.toResponse(post, likeCnt, commentCnt);
    }

    /**
     * 모든 게시물을 조회합니다. (삭제되지 않은 것만)
     *
     * @param pageable 페이지 정보
     * @return 게시물 DTO 페이지
     */
    @Transactional(readOnly = true)
    public Page<PostDto> getAllPost(Pageable pageable) {

        // DELETED_POST_FILTER를 활성화하여 삭제된 게시물만 조회하도록 설정합니다.
        filterManager.enableFilter(FilterConstants.DELETED_POST_FILTER, FilterConstants.DELETED_POST_AT_PARAM, false);

        // 페이지 정보를 이용하여 게시물을 조회합니다.
        Page<Post> posts = postRepository.getPosts(pageable);

        // DELETED_POST_FILTER를 비활성화하여 원래 상태로 복구합니다.
        filterManager.disableFilter(FilterConstants.DELETED_POST_FILTER);

        // DTO로 변환
        return PostDto.toDtoList(posts);
    }

    public PostDto update(Integer postId, String userName, String body) {
        // 사용자 정보 및 게시물 정보 가져오기
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new SnsAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage()));

        // 권한 확인 및 게시물 업데이트
        if (checkAuth(userName, post, user)) {
            throw new SnsAppException(INVALID_PERMISSION, INVALID_PERMISSION.getMessage());
        }
        post.updatePost(body);
        return PostDto.toPostDto(post);
    }

    public boolean delete(String userName, Integer postId) {
        // 사용자 정보 및 게시물 정보 가져오기
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new SnsAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage()));
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        // 권한 확인 및 게시물 삭제
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

    @Transactional(readOnly = true)
    public Page<PostDto> getMyPost(Pageable pageable, String userName) {
        // 사용자 정보 가져오기 및 해당 사용자의 게시물 조회
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));
        Page<Post> posts = postRepository.findAllByUserId(pageable, user.getId());
        Page<PostDto> myPosts = PostDto.toDtoList(posts);
        return myPosts;
    }

    /**
     * 삭제된 게시물을 조회합니다.
     *
     * @param pageable 페이지 정보
     * @param userName 사용자 이름
     * @return 삭제된 게시물 DTO 페이지
     */
    @Transactional(readOnly = true)
    public Page<PostDto> getAllDeletedPost(Pageable pageable, String userName) {
        // 사용자 정보 가져오기 및 해당 사용자의 삭제된 게시물 조회
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        // DELETED_POST_FILTER를 활성화하여 삭제된 게시물만 조회하도록 설정합니다.
        filterManager.enableFilter(FilterConstants.DELETED_POST_FILTER, 			FilterConstants.DELETED_POST_AT_PARAM, true);

        // 페이지 정보를 이용하여 게시물을 조회합니다.
        Page<Post> posts = postRepository.getPosts(pageable);

        // DELETED_POST_FILTER를 비활성화하여 원래 상태로 복구합니다.
        filterManager.disableFilter(FilterConstants.DELETED_POST_FILTER);

        // DTO로 변환
        return PostDto.toDtoList(posts);
    }
}
