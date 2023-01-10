package com.likelionsns.final_project.controller;

import com.likelionsns.final_project.domain.dto.PostDto;
import com.likelionsns.final_project.domain.request.PostCreateRequest;
import com.likelionsns.final_project.domain.request.PostUpdateRequest;
import com.likelionsns.final_project.domain.response.*;
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
    private final LikeService likeService;

    @ApiOperation(value = "포스트 등록")
    @PostMapping
    public ResponseEntity<Response<PostResponse>> createPost(@RequestBody PostCreateRequest postCreateRequest, Authentication authentication) {
        PostDto postDto = postService.createPost(postCreateRequest, authentication.getName());
        return ResponseEntity.ok().body(Response.success(new PostResponse("포스트 등록 완료", postDto.getId())));
    }

    @ApiOperation(value = "포스트 상세 보기")
    @GetMapping("/{postId}")
    public ResponseEntity<Response<PostDto>>findById(@PathVariable Integer postId) {
        PostDto postDto = postService.findDetail(postId);
        return ResponseEntity.ok().body(Response.success(postDto));
    }
    @ApiOperation(value = "포스트 목록")
    @GetMapping
    public ResponseEntity<Response<Page<PostDto>>> getPostList(@PageableDefault(size = 20)
                                               @SortDefault(sort = "registeredAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostDto> postDtos = postService.getAllItems(pageable);
        return ResponseEntity.ok().body(Response.success(postDtos));
    }
    @ApiOperation(value = "포스트 수정")
    @PutMapping("/{postId}")
    public ResponseEntity<Response<PostResponse>>update(@PathVariable Integer postId, @RequestBody PostUpdateRequest postUpdateRequest, Authentication authentication) {
        PostDto postDto = postService.update(postId, authentication.getName(), postUpdateRequest.getTitle(), postUpdateRequest.getBody());
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
        Page<PostDto> myPosts = postService.getMyPost(pageable,authentication.getName());
        return ResponseEntity.accepted().body(Response.success(myPosts));
    }

}
