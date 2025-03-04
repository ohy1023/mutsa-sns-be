package com.likelionsns.final_project.domain.request;

import com.likelionsns.final_project.domain.entity.Post;
import com.likelionsns.final_project.domain.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostCreateRequest {
    private String body;

    @Builder
    public PostCreateRequest(String body) {
        this.body = body;
    }

    public Post toEntity(User user) {
        return Post.builder()
                .body(this.body)
                .user(user)
                .build();
    }

}
