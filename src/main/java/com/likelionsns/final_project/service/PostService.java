package com.likelionsns.final_project.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelionsns.final_project.domain.constant.FilterConstants;
import com.likelionsns.final_project.domain.dto.PostDto;
import com.likelionsns.final_project.domain.entity.Post;
import com.likelionsns.final_project.domain.entity.PostMedia;
import com.likelionsns.final_project.domain.entity.User;
import com.likelionsns.final_project.domain.response.PostDetailResponse;
import com.likelionsns.final_project.domain.response.PostSummaryInfoResponse;
import com.likelionsns.final_project.exception.SnsAppException;
import com.likelionsns.final_project.repository.*;
import com.likelionsns.final_project.utils.FilterManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

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
    private final FilterManager filterManager;
    private final AwsS3Service awsS3Service;
    private final CommentRepository commentRepository;
    private final PostMediaRepository postMediaRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void createPost(String body, List<MultipartFile> multipartFileList, String multipartFileOrderList, String userName) {
        User user = findUserByUserName(userName);

        // 게시글 생성
        Post post = Post.builder()
                .user(user)
                .body(body)
                .build();

        // JSON 문자열로 전달된 `multipartFileOrderList`를 List<Integer>로 변환
        List<Integer> orderList;
        try {
            orderList = objectMapper.readValue(multipartFileOrderList, new TypeReference<List<Integer>>() {
            });
        } catch (Exception e) {
            throw new SnsAppException(INVALID_MEDIA_ORDER_LIST, INVALID_MEDIA_ORDER_LIST.getMessage());
        }

        // 파일 개수와 순서 개수가 일치하는지 검증
        if (multipartFileList.size() != orderList.size()) {
            throw new SnsAppException(MEDIA_COUNT_MISMATCH, MEDIA_COUNT_MISMATCH.getMessage());
        }

        // 파일과 순서를 매칭한 리스트 생성
        List<PostMedia> mediaList = new ArrayList<>();
        for (int i = 0; i < multipartFileList.size(); i++) {
            String multipartUrl = awsS3Service.uploadPostOriginImage(multipartFileList.get(i));

            mediaList.add(PostMedia.builder()
                    .mediaUrl(multipartUrl)
                    .mediaOrder(orderList.get(i)) // JSON에서 변환한 정수형 리스트 사용
                    .build());
        }

        // 연관 관계 설정
        mediaList.forEach(post::addMedia);

        // 게시글 저장
        postRepository.save(post);
    }

    @Transactional
    public void updatePost(Integer postId, String body, List<MultipartFile> multipartFileList, String multipartFileOrderList, List<String> existingMediaUrls, String userName) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new SnsAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage()));

        // 유저 검증
        if (!post.getUser().getUserName().equals(userName)) {
            throw new SnsAppException(INVALID_PERMISSION, INVALID_PERMISSION.getMessage());
        }

        // 본문 수정
        post.updateBody(body);

        // JSON 문자열로 전달된 `multipartFileOrderList`를 List<Integer>로 변환
        List<Integer> orderList;
        try {
            orderList = objectMapper.readValue(multipartFileOrderList, new TypeReference<List<Integer>>() {});
        } catch (Exception e) {
            throw new SnsAppException(INVALID_MEDIA_ORDER_LIST, INVALID_MEDIA_ORDER_LIST.getMessage());
        }

        // 기존 미디어 URL을 기반으로 유지할 미디어만 필터링
        List<PostMedia> existingMedia = post.getMediaList().stream()
                .filter(media -> existingMediaUrls.contains(media.getMediaUrl()))
                .collect(Collectors.toList());

        // 기존 미디어에서 삭제된 것들 제거
        post.getMediaList().removeIf(media -> !existingMediaUrls.contains(media.getMediaUrl()));

        // 새로운 미디어 추가
        if (multipartFileList != null && !multipartFileList.isEmpty()) {
            for (int i = 0; i < multipartFileList.size(); i++) {
                String multipartUrl = awsS3Service.uploadPostOriginImage(multipartFileList.get(i));

                existingMedia.add(PostMedia.builder()
                        .mediaUrl(multipartUrl)
                        .mediaOrder(orderList.get(i)) // JSON에서 변환한 정수형 리스트 사용
                        .build());
            }
        }

        // 미디어 순서 업데이트
        for (int i = 0; i < existingMedia.size(); i++) {
            existingMedia.get(i).updateOrder(orderList.get(i));
        }

        // 최종 미디어 리스트 적용
        post.setMediaList(existingMedia);

        // 게시글 저장
        postRepository.save(post);
    }


    public boolean delete(String userName, Integer postId) {
        // 사용자 정보 및 게시물 정보 가져오기
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new SnsAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage()));
        User user = findUserByUserName(userName);

        // 권한 확인 및 게시물 삭제
        if (checkAuth(userName, post, user)) {
            throw new SnsAppException(INVALID_PERMISSION, INVALID_PERMISSION.getMessage());
        }
        commentRepository.deleteAllByPost(post);
        likeRepository.deleteAllByPost(post);
        postRepository.delete(post);
        return true;
    }

    @Transactional(readOnly = true)
    public Page<PostDetailResponse> getFollowingFeed(String userName, Pageable pageable) {
        User user = findUserByUserName(userName);

        // 본인 + 팔로우한 유저 ID 목록 조회
        List<Integer> followingUserIds = user.getFollowingList()
                .stream()
                .map(follow -> follow.getFollowing().getId())
                .collect(Collectors.toList());

        followingUserIds.add(user.getId()); // 본인 ID 포함

        // 해당 유저들이 작성한 피드 가져오기
        Page<Post> posts = postRepository.findByUserIdIn(followingUserIds, pageable);

        // PostDetailResponse 로 변환하여 반환
        return posts.map(post -> {
            List<PostMedia> postMediaList = postMediaRepository.findPostMediaByPost(post);
            Long likeCnt = likeRepository.countByPost(post);
            Long commentCnt = commentRepository.countByPost(post);

            Boolean isOwner = post.getUser().equals(user);
            Boolean isLiked = likeRepository.existsByPostAndUser(post, user);

            return PostDetailResponse.toResponse(post, isOwner, isLiked, postMediaList, likeCnt, commentCnt);
        });
    }


    @Transactional(readOnly = true)
    public PostDetailResponse getDetailPost(Integer postId, String userName) {
        User user = findUserByUserName(userName);
        // 게시물 상세 정보 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new SnsAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage()));

        // 좋아요 개수 및 댓글 개수 조회
        Long likeCnt = likeRepository.countByPost(post);
        Long commentCnt = commentRepository.countByPost(post);

        List<PostMedia> postMediaList = postMediaRepository.findPostMediaByPost(post);

        Boolean isOwner = post.getUser().equals(user);
        Boolean isLiked = likeRepository.existsByPostAndUser(post, user);

        return PostDetailResponse.toResponse(post, isOwner, isLiked, postMediaList, likeCnt, commentCnt);
    }

    @Transactional(readOnly = true)
    public Page<PostSummaryInfoResponse> getUserPost(String userName, Pageable pageable) {
        // 사용자 정보 가져오기 및 해당 사용자의 게시물 조회
        User user = findUserByUserName(userName);
        Page<Post> posts = postRepository.findAllByUserId(user.getId(), pageable);
        return posts.map(post -> PostSummaryInfoResponse.builder()
                .postId(post.getId())
                .registeredAt(post.getRegisteredAt())
                .postThumbnailUrl(postMediaRepository.findThumbnailUrl(post, PageRequest.of(0, 1)).stream().findFirst().orElse(null))
                .build()
        );
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
        User user = findUserByUserName(userName);

        // DELETED_POST_FILTER를 활성화하여 삭제된 게시물만 조회하도록 설정합니다.
        filterManager.enableFilter(FilterConstants.DELETED_POST_FILTER, FilterConstants.DELETED_POST_AT_PARAM, true);

        // 페이지 정보를 이용하여 게시물을 조회합니다.
        Page<Post> posts = postRepository.getPosts(pageable);

        // DELETED_POST_FILTER를 비활성화하여 원래 상태로 복구합니다.
        filterManager.disableFilter(FilterConstants.DELETED_POST_FILTER);

        // DTO로 변환
        return PostDto.toDtoList(posts);
    }

    private User findUserByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));
    }

    private static boolean checkAuth(String userName, Post post, User user) {
        return !user.getUserRole().equals(ADMIN) && !userName.equals(post.getUser().getUserName());
    }
}
