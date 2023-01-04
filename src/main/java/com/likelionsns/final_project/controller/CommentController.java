package com.likelionsns.final_project.controller;

import com.likelionsns.final_project.domain.dto.CommentDto;
import com.likelionsns.final_project.domain.request.CommentCreateRequest;
import com.likelionsns.final_project.domain.request.CommentUpdateRequest;
import com.likelionsns.final_project.domain.response.CommentResponse;
import com.likelionsns.final_project.domain.response.CommentUpdateResponse;
import com.likelionsns.final_project.domain.response.Response;
import com.likelionsns.final_project.service.CommentService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/posts")
public class CommentController {
    private final CommentService commentService;

    @ApiOperation(value = "댓글 작성")
    @PostMapping("/{postId}/comments")
    public Response<CommentDto> createComment(@PathVariable Integer postId, @RequestBody CommentCreateRequest commentCreateRequest, Authentication authentication) {
        CommentDto commentDto = commentService.createComment(postId, authentication.getName(), commentCreateRequest);
        return Response.success(commentDto);
    }

    @ApiOperation(value = "댓글 목록",notes = "해당 post_id의 댓글 목록 조회")
    @GetMapping("{postId}/comments")
    public Response<Page<CommentDto>> getPostList(@PageableDefault(size = 10)
                                                  @SortDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CommentDto> commentDtos = commentService.getAllItems(pageable);
        return Response.success(commentDtos);
    }

    @ApiOperation(value = "댓글 수정")
    @PutMapping("/{postId}/comments/{commentId}")
    public Response<CommentUpdateResponse> updateComment(@PathVariable Integer postId, @PathVariable Integer commentId, @RequestBody CommentUpdateRequest commentUpdateRequest, Authentication authentication) {
        CommentUpdateResponse commentUpdateResponse = commentService.updateComment(postId, commentId, commentUpdateRequest, authentication.getName());
        return Response.success(commentUpdateResponse);
    }

    @ApiOperation(value = "댓글 삭제",notes = "soft delete")
    @DeleteMapping("/{postId}/comments/{commentId}")
    public Response<CommentResponse> deleteComment(@PathVariable Integer postId, @PathVariable Integer commentId, Authentication authentication) {

        commentService.deleteComment(postId, commentId, authentication.getName());

        return Response.success(new CommentResponse("댓글 삭제 완료", commentId));
    }
}
