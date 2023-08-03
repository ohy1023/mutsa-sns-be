package com.likelionsns.final_project.domain.response;

import com.likelionsns.final_project.domain.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailResponse {
    private Integer id;
    private String title;
    private String body;
    private String userName;
    private String createdAt;
    private Long likeCnt;

    private Long commentCnt;

    public static PostDetailResponse toResponse(Post post, Long likeCnt, Long commentCnt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");
        return PostDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .userName(post.getUser().getUserName())
                .body(post.getBody())
                .createdAt(post.getRegisteredAt().format(formatter))
                .likeCnt(likeCnt)
                .commentCnt(commentCnt)
                .build();
    }
}
