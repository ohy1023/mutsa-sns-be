package com.likelionsns.final_project.domain.request;

import lombok.Data;

import java.util.List;

@Data
public class PostCreateRequest {
    private String body;
    private List<PostMediaRequest> mediaList;
}
