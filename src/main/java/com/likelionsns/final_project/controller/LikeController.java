package com.likelionsns.final_project.controller;

import com.likelionsns.final_project.domain.response.Response;
import com.likelionsns.final_project.service.LikeService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class LikeController {

    private final LikeService likeService;

    @ApiOperation(value = "좋아요 누르기")
    @PostMapping("/{postId}/likes")
    public Response<String> addCount(@PathVariable Integer postId, Authentication authentication) {
        likeService.addCount(postId, authentication.getName());
        return Response.success("좋아요를 눌렀습니다.");
    }

    @ApiOperation(value = "좋아요 개수 조회")
    @GetMapping("/{postId}/likes")
    public Response<Long> viewCount(@PathVariable Integer postId) {
        Long likeCnt = likeService.viewCount(postId);
        return Response.success(likeCnt);
    }


}
