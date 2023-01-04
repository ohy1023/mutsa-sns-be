package com.likelionsns.final_project.controller;

import com.likelionsns.final_project.domain.dto.CommentDto;
import com.likelionsns.final_project.domain.dto.PostDto;
import com.likelionsns.final_project.domain.entity.Comment;
import com.likelionsns.final_project.domain.entity.Post;
import com.likelionsns.final_project.domain.request.CommentCreateRequest;
import com.likelionsns.final_project.domain.request.PostCreateRequest;
import com.likelionsns.final_project.domain.request.PostUpdateRequest;
import com.likelionsns.final_project.domain.response.*;
import com.likelionsns.final_project.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    @PostMapping
    public Response<PostResponse> createPost(@RequestBody PostCreateRequest postCreateRequest, Authentication authentication) {
        PostDto postDto = postService.createPost(postCreateRequest, authentication.getName());
        return Response.success(new PostResponse("포스트 등록 완료", postDto.getId()));
    }


    @GetMapping("/{postId}")
    public Response<PostDto> findById(@PathVariable Integer postId) {
        PostDto postDto = postService.findDetail(postId);
        return Response.success(postDto);
    }

    @GetMapping
    public Response<Page<PostDto>> getPostList(@PageableDefault(size = 20)
                                               @SortDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostDto> postDtos = postService.getAllItems(pageable);
        return Response.success(postDtos);
    }

    @PutMapping("/{postId}")
    public Response<PostResponse> update(@PathVariable Integer postId, @RequestBody PostUpdateRequest postUpdateRequest, Authentication authentication) {
        PostDto postDto = postService.update(postId, authentication.getName(), postUpdateRequest.getTitle(), postUpdateRequest.getBody());
        return Response.success(new PostResponse("포스트 수정 완료", postId));
    }


    @DeleteMapping("/{postId}")
    public Response<PostResponse> deleteById(@PathVariable Integer postId, Authentication authentication) {
        postService.delete(authentication.getName(), postId);
        return Response.success(new PostResponse("포스트 삭제 완료", postId));
    }

    @GetMapping("/my")
    public Response<Page<PostDto>> getMyPost(@PageableDefault(size = 20)
                                                   @SortDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,Authentication authentication) {
        Page<PostDto> myPosts = postService.getMyPost(pageable,authentication.getName());
        return Response.success(myPosts);
    }

}
