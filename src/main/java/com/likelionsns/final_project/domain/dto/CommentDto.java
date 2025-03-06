package com.likelionsns.final_project.domain.dto;

import com.likelionsns.final_project.domain.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class CommentDto {
    private Integer id;
    private String comment;
    private String userName;
    private String userImg;
    private Integer postId;
    private String registeredAt;

    @Builder
    public CommentDto(Integer id, String comment, String userName,String userImg, Integer postId, String registeredAt) {
        this.id = id;
        this.comment = comment;
        this.userName = userName;
        this.userImg = userImg;
        this.postId = postId;
        this.registeredAt = registeredAt;
    }

    public static CommentDto toCommentDto(Comment comment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");
        return CommentDto.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .userName(comment.getUser().getUserName())
                .userImg(comment.getUser().getUserImg())
                .postId(comment.getPost().getId())
                .registeredAt(comment.getRegisteredAt().format(formatter))
                .build();
    }

    public static Page<CommentDto> toDtoList(Page<Comment> comments) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");
        return comments.map(m -> CommentDto.builder()
                .id(m.getId())
                .comment(m.getComment())
                .userName(m.getUser().getUserName())
                .userImg(m.getUser().getUserImg())
                .postId(m.getPost().getId())
                .registeredAt(m.getRegisteredAt().format(formatter))
                .build());
    }
}
