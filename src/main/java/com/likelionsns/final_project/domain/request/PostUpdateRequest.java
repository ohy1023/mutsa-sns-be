package com.likelionsns.final_project.domain.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostUpdateRequest {
    private String body;

    @Builder
    public PostUpdateRequest(String body) {
        this.body = body;
    }
}
