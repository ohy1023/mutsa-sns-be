package com.likelionsns.final_project.domain.dto;

import com.likelionsns.final_project.domain.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class PostDto {

    private Integer id;
    private String body;
    private String userName;
    private String createdAt;

    @Builder
    public PostDto(Integer id, String body, String userName, String createdAt) {
        this.id = id;
        this.body = body;
        this.userName = userName;
        this.createdAt = createdAt;
    }

    public static PostDto toPostDto(Post post) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");
        String formattedDateTime = post.getRegisteredAt().format(formatter);
        return PostDto.builder()
                .id(post.getId())
                .body(post.getBody())
                .userName(post.getUser().getUserName())
                .createdAt(formattedDateTime)
                .build();
    }

    public static Page<PostDto> toDtoList(Page<Post> postEntities) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");
        return postEntities.map(m -> PostDto.builder()
                .id(m.getId())
                .body(m.getBody())
                .userName(m.getUser().getUserName())
                .createdAt(m.getRegisteredAt().format(formatter))
                .build());
    }
}
