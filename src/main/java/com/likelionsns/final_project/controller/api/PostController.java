package com.likelionsns.final_project.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelionsns.final_project.domain.dto.CommentDto;
import com.likelionsns.final_project.domain.dto.PostDto;
import com.likelionsns.final_project.domain.request.*;
import com.likelionsns.final_project.domain.response.*;
import com.likelionsns.final_project.service.CommentService;
import com.likelionsns.final_project.service.LikeService;
import com.likelionsns.final_project.service.PostService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;

    @ApiOperation(value = "포스트 등록")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadPost(
            @RequestPart("postData") String postDataJson,
            @RequestPart(value = "multipartFileList", required = false) List<MultipartFile> multipartFileList,
            @RequestPart(value = "multipartFileOrderList", required = false) String multipartFileOrderList,
            Authentication authentication
    ) {

        ObjectMapper objectMapper = new ObjectMapper();
        PostCreateRequest postData;
        try {
            postData = objectMapper.readValue(postDataJson, PostCreateRequest.class);
        } catch (JsonProcessingException e) {
            log.error("파싱 오류");
            return null;
        }

        postService.createPost(postData.getBody(), multipartFileList, multipartFileOrderList, authentication.getName());

        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "포스트 수정")
    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updatePost(
            @PathVariable Integer postId,
            @RequestPart("postData") String postDataJson,
            @RequestPart(value = "multipartFileList", required = false) List<MultipartFile> multipartFileList,
            @RequestPart(value = "multipartFileOrderList", required = false) String multipartFileOrderList,
            @RequestPart(value = "existingMediaUrls", required = false) String existingMediaUrlsJson,
            Authentication authentication
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        PostUpdateRequest postData;
        List<String> existingMediaUrls;

        try {
            postData = objectMapper.readValue(postDataJson, PostUpdateRequest.class);
            existingMediaUrls = objectMapper.readValue(existingMediaUrlsJson, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            log.error("파싱 오류", e);
            return ResponseEntity.badRequest().build();
        }

        postService.updatePost(postId, postData.getBody(), multipartFileList, multipartFileOrderList, existingMediaUrls, authentication.getName());

        return ResponseEntity.noContent().build();
    }


    @ApiOperation(value = "포스트 삭제", notes = "soft delete 사용")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer postId, Authentication authentication) {
        postService.delete(authentication.getName(), postId);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "포스트 상세 조회")
    @GetMapping("/{postId}")
    public ResponseEntity<Response<PostDetailResponse>> getDetailPost(@PathVariable Integer postId, Authentication authentication) {
        return ResponseEntity.ok().body(Response.success(postService.getDetailPost(postId, authentication.getName())));
    }

    @ApiOperation(value = "내가 팔로우한 사람의 피드 목록 (자기 포함)")
    @GetMapping("/following")
    public ResponseEntity<Response<Page<PostDetailResponse>>> getFollowingFeed(
            Authentication authentication,
            @PageableDefault(size = 6)
            @SortDefault(sort = "registeredAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PostDetailResponse> followingFeed = postService.getFollowingFeed(authentication.getName(), pageable);
        return ResponseEntity.ok().body(Response.success(followingFeed));
    }


    @ApiOperation(value = "나의 피드 요약 목록")
    @GetMapping("/my")
    public ResponseEntity<Response<Page<PostSummaryInfoResponse>>> getMyPost(@PageableDefault(size = 9)
                                                                             @SortDefault(sort = "registeredAt", direction = Sort.Direction.DESC) Pageable pageable, Authentication authentication) {
        Page<PostSummaryInfoResponse> myPosts = postService.getUserPost(authentication.getName(), pageable);
        return ResponseEntity.ok().body(Response.success(myPosts));
    }

    @ApiOperation(value = "해당 유저의 피드 요약 목록")
    @GetMapping("/info/{userName}")
    public ResponseEntity<Response<Page<PostSummaryInfoResponse>>> getMyPost(@PathVariable String userName, @PageableDefault(size = 9)
    @SortDefault(sort = "registeredAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostSummaryInfoResponse> myPosts = postService.getUserPost(userName, pageable);
        return ResponseEntity.ok().body(Response.success(myPosts));
    }

    @ApiOperation(value = "삭제된 피드 목록", notes = "삭제 된 포스트 목록")
    @GetMapping("/deleted")
    public Response<Page<PostDto>> getDeletedPost(@PageableDefault(size = 20)
                                                  @SortDefault(sort = "deletedAt", direction = Sort.Direction.DESC) Pageable pageable, Authentication authentication) {
        String userName = authentication.getName();
        Page<PostDto> deletedPosts = postService.getAllDeletedPost(pageable, userName);

        return Response.success(deletedPosts);
    }

    @ApiOperation(value = "댓글 작성")
    @PostMapping("/{postId}/comments")
    public ResponseEntity<Response<CommentDto>> createComment(@PathVariable Integer postId, @RequestBody CommentCreateRequest commentCreateRequest, Authentication authentication) {
        return ResponseEntity.ok().body(Response.success(commentService.createComment(postId, authentication.getName(), commentCreateRequest)));
    }

    @ApiOperation(value = "댓글 목록", notes = "해당 post_id의 댓글 목록 조회")
    @GetMapping("{postId}/comments")
    public ResponseEntity<Response<Page<CommentDto>>> getPostList(@PathVariable Integer postId, @PageableDefault(size = 10)
    @SortDefault(sort = "registeredAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(Response.success(commentService.getAllItems(postId, pageable)));
    }

    @ApiOperation(value = "댓글 수정")
    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Response<CommentUpdateResponse>> updateComment(@PathVariable Integer postId, @PathVariable Integer commentId, @RequestBody CommentUpdateRequest commentUpdateRequest, Authentication authentication) {
        return ResponseEntity.accepted().body(Response.success(commentService.updateComment(postId, commentId, commentUpdateRequest, authentication.getName())));
    }

    @ApiOperation(value = "댓글 삭제", notes = "soft delete")
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer postId, @PathVariable Integer commentId, Authentication authentication) {

        commentService.deleteComment(postId, commentId, authentication.getName());

        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "좋아요 누르기")
    @PostMapping("/{postId}/likes")
    public ResponseEntity<Void> addCount(@PathVariable Integer postId, Authentication authentication) {
        likeService.addCount(postId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "좋아요 취소")
    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<Void> deleteCount(@PathVariable Integer postId, Authentication authentication) {
        likeService.deleteCount(postId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "좋아요 개수 조회")
    @GetMapping("/{postId}/likes")
    public ResponseEntity<Response<Long>> viewCount(@PathVariable Integer postId) {
        Long likeCnt = likeService.viewCount(postId);
        return ResponseEntity.ok().body(Response.success(likeCnt));
    }


    @ApiOperation(value = "댓글 개수 조회")
    @GetMapping("/{postId}/comment-cnt")
    public ResponseEntity<Response<Long>> viewCommentCount(@PathVariable Integer postId) {
        Long commentCnt = commentService.viewCount(postId);
        return ResponseEntity.ok().body(Response.success(commentCnt));
    }
}
