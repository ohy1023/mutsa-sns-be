package com.likelionsns.final_project.controller.api;

import com.likelionsns.final_project.domain.dto.CommentDto;
import com.likelionsns.final_project.domain.dto.PostDto;
import com.likelionsns.final_project.domain.request.CommentCreateRequest;
import com.likelionsns.final_project.domain.request.CommentUpdateRequest;
import com.likelionsns.final_project.domain.request.PostCreateRequest;
import com.likelionsns.final_project.domain.request.PostUpdateRequest;
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
    public ResponseEntity<Response<PostResponse>> createPost(@RequestBody PostCreateRequest postCreateRequest, Authentication authentication) {
        PostDto postDto = postService.createPost(postCreateRequest, authentication.getName());
        return ResponseEntity.ok().body(Response.success(new PostResponse("포스트 등록 완료", postDto.getId())));
    }

    @ApiOperation(value = "포스트 상세 보기")
    @GetMapping("/{postId}")
    public ResponseEntity<Response<PostDetailResponse>> findById(@PathVariable Integer postId) {
        PostDetailResponse response = postService.findDetail(postId);
        return ResponseEntity.ok().body(Response.success(response));
    }

    @ApiOperation(value = "포스트 목록")
    @GetMapping
    public ResponseEntity<Response<Page<PostDto>>> getPostList(@PageableDefault(size = 9)
                                                               @SortDefault(sort = "registeredAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostDto> postDtos = postService.getAllPost(pageable);
        return ResponseEntity.ok().body(Response.success(postDtos));
    }

    @ApiOperation(value = "포스트 수정")
    @PutMapping("/{postId}")
    public ResponseEntity<Response<PostResponse>> update(@PathVariable Integer postId, @RequestBody PostUpdateRequest postUpdateRequest, Authentication authentication) {
        PostDto postDto = postService.update(postId, authentication.getName(), postUpdateRequest.getBody());
        return ResponseEntity.ok().body(Response.success(new PostResponse("포스트 수정 완료", postId)));
    }

    @ApiOperation(value = "포스트 삭제", notes = "soft delete 사용 cf) post, like, comment 같이 삭제")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Response<PostResponse>> deleteById(@PathVariable Integer postId, Authentication authentication) {
        postService.delete(authentication.getName(), postId);
        return ResponseEntity.ok().body(Response.success(new PostResponse("포스트 삭제 완료", postId)));
    }

    @ApiOperation(value = "나의 피드 목록", notes = "로그인한 유저의 포스트 목록")
    @GetMapping("/my")
    public ResponseEntity<Response<Page<PostDto>>> getMyPost(@PageableDefault(size = 20)
                                                             @SortDefault(sort = "registeredAt", direction = Sort.Direction.DESC) Pageable pageable, Authentication authentication) {
        Page<PostDto> myPosts = postService.getMyPost(pageable, authentication.getName());
        return ResponseEntity.accepted().body(Response.success(myPosts));
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

}
