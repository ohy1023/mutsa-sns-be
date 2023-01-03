package com.likelionsns.final_project.domain.dto;

import com.likelionsns.final_project.domain.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentDto {
    private Integer id;
    private String comment;
    private String userName;
    private Integer postId;
    private LocalDateTime createdAt;

    @Builder
    public CommentDto(Integer id, String comment, String userName, Integer postId, LocalDateTime createdAt) {
        this.id = id;
        this.comment = comment;
        this.userName = userName;
        this.postId = postId;
        this.createdAt = createdAt;
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .userName(comment.getUser().getUserName())
                .postId(comment.getPost().getId())
                .createdAt(comment.getRegisteredAt())
                .build();
    }

    public static Page<CommentDto> toDtoList(Page<Comment> comments){
        Page<CommentDto> commentDtoList = comments.map(m -> CommentDto.builder()
                .id(m.getId())
                .comment(m.getComment())
                .userName(m.getUser().getUserName())
                .postId(m.getPost().getId())
                .createdAt(m.getRegisteredAt())
                .build());
        return commentDtoList;
    }
}
