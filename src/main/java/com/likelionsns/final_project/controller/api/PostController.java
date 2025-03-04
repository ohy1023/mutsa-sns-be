package com.likelionsns.final_project.controller.api;

import com.likelionsns.final_project.domain.dto.CommentDto;
import com.likelionsns.final_project.domain.dto.PostDto;
import com.likelionsns.final_project.domain.request.*;
import com.likelionsns.final_project.domain.response.*;
import com.likelionsns.final_project.service.CommentService;
import com.likelionsns.final_project.service.LikeService;
import com.likelionsns.final_project.service.PostService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;

    @ApiOperation(value = "포스트 등록")
    @PostMapping
    public ResponseEntity<Void> createPost(@RequestPart PostCreateRequest postCreateRequest, Authentication authentication) {
        postService.createPost(postCreateRequest, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "포스트 수정")
    @PutMapping("/{postId}")
    public ResponseEntity<Void> update(@PathVariable Integer postId, @RequestPart PostUpdateRequest postUpdateRequest, Authentication authentication) {
        postService.updatePost(postId, authentication.getName(), postUpdateRequest);
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
    public ResponseEntity<Response<PostDetailResponse>> getDetailPost(@PathVariable Integer postId) {
        return ResponseEntity.ok().body(Response.success(postService.getDetailPost(postId)));
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
    @GetMapping("/{userName}")
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
        CommentDto commentDto = commentService.createComment(postId, authentication.getName(), commentCreateRequest);
        return ResponseEntity.ok().body(Response.success(commentDto));
    }

    @ApiOperation(value = "댓글 목록", notes = "해당 post_id의 댓글 목록 조회")
    @GetMapping("{postId}/comments")
    public ResponseEntity<Response<Page<CommentDto>>> getPostList(@PathVariable Integer postId, @PageableDefault(size = 10)
    @SortDefault(sort = "registeredAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CommentDto> commentDtos = commentService.getAllItems(postId, pageable);
        return ResponseEntity.ok().body(Response.success(commentDtos));
    }

    @ApiOperation(value = "댓글 수정")
    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Response<CommentUpdateResponse>> updateComment(@PathVariable Integer postId, @PathVariable Integer commentId, @RequestBody CommentUpdateRequest commentUpdateRequest, Authentication authentication) {
        CommentUpdateResponse commentUpdateResponse = commentService.updateComment(postId, commentId, commentUpdateRequest, authentication.getName());
        return ResponseEntity.accepted().body(Response.success(commentUpdateResponse));
    }

    @ApiOperation(value = "댓글 삭제", notes = "soft delete")
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Response<CommentResponse>> deleteComment(@PathVariable Integer postId, @PathVariable Integer commentId, Authentication authentication) {

        commentService.deleteComment(postId, commentId, authentication.getName());

        return ResponseEntity.ok().body(Response.success(new CommentResponse("댓글 삭제 완료", commentId)));
    }

    @ApiOperation(value = "좋아요 누르기")
    @PostMapping("/{postId}/likes")
    public ResponseEntity<Response<String>> addCount(@PathVariable Integer postId, Authentication authentication) {
        likeService.addCount(postId, authentication.getName());
        return ResponseEntity.ok().body(Response.success("좋아요를 눌렀습니다."));
    }

    @ApiOperation(value = "좋아요 취소")
    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<Response<String>> deleteCount(@PathVariable Integer postId, Authentication authentication) {
        likeService.deleteCount(postId, authentication.getName());
        return ResponseEntity.ok().body(Response.success("좋아요를 취소했습니다."));
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
