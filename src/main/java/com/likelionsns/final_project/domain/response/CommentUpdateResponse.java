package com.likelionsns.final_project.domain.response;

import com.likelionsns.final_project.domain.dto.CommentDto;
import com.likelionsns.final_project.domain.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentUpdateResponse {
    private Integer id;
    private String comment;
    private String userName;
    private Integer postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public CommentUpdateResponse(Integer id, String comment, String userName, Integer postId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.comment = comment;
        this.userName = userName;
        this.postId = postId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CommentUpdateResponse toResponse(Comment comment) {
        return CommentUpdateResponse.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .userName(comment.getUser().getUserName())
                .postId(comment.getPost().getId())
                .createdAt(comment.getRegisteredAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
