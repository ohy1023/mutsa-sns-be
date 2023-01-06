package com.likelionsns.final_project.domain.dto;

import com.likelionsns.final_project.domain.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostDto {

    private Integer id;
    private String title;
    private String body;
    private String userName;
    private LocalDateTime createdAt;

    @Builder
    public PostDto(Integer id, String title, String body, String userName, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.userName = userName;
        this.createdAt = createdAt;
    }

    public static PostDto toPostDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .userName(post.getUser().getUserName())
                .createdAt(post.getRegisteredAt())
                .build();
    }

    /* Page<Entity> -> Page<Dto> 변환처리 */
    public static Page<PostDto> toDtoList(Page<Post> postEntities) {
        Page<PostDto> postDtoList = postEntities.map(m -> PostDto.builder()
                .id(m.getId())
                .title(m.getTitle())
                .body(m.getBody())
                .userName(m.getUser().getUserName())
                .createdAt(m.getRegisteredAt())
                .build());
        return postDtoList;
    }
}
