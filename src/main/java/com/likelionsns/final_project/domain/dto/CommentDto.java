package com.likelionsns.final_project.domain.dto;

import com.likelionsns.final_project.domain.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class CommentDto {
    private Integer id;
    private String comment;
    private String userName;
    private Integer postId;
    private String createdAt;

    @Builder
    public CommentDto(Integer id, String comment, String userName, Integer postId, String createdAt) {
        this.id = id;
        this.comment = comment;
        this.userName = userName;
        this.postId = postId;
        this.createdAt = createdAt;
    }

    public static CommentDto toCommentDto(Comment comment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");
        return CommentDto.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .userName(comment.getUser().getUserName())
                .postId(comment.getPost().getId())
                .createdAt(comment.getRegisteredAt().format(formatter))
                .build();
    }

    public static Page<CommentDto> toDtoList(Page<Comment> comments) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");
        Page<CommentDto> commentDtoList = comments.map(m -> CommentDto.builder()
                .id(m.getId())
                .comment(m.getComment())
                .userName(m.getUser().getUserName())
                .postId(m.getPost().getId())
                .createdAt(m.getRegisteredAt().format(formatter))
                .build());
        return commentDtoList;
    }
}
