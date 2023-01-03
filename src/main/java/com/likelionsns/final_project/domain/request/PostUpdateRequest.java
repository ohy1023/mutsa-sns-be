package com.likelionsns.final_project.domain.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostUpdateRequest {
    private String title;
    private String body;

    @Builder
    public PostUpdateRequest(String title, String body) {
        this.title = title;
        this.body = body;
    }
}
