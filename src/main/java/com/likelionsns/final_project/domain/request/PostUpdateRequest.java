package com.likelionsns.final_project.domain.request;

import lombok.Data;
import java.util.List;

@Data
public class PostUpdateRequest {
    private String body; // 게시글 내용
    private List<PostMediaUpdateRequest> mediaList; // 전체 미디어 리스트 (순서 반영)
}
