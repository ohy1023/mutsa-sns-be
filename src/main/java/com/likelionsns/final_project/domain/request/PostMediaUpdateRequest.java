package com.likelionsns.final_project.domain.request;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class PostMediaUpdateRequest {
    private Integer id; // 기존 미디어 ID (새로운 미디어라면 null)
    private MultipartFile file; // 새롭게 추가하는 파일 (기존 미디어라면 null)
    private String url; // 기존 S3 URL (새로운 미디어라면 null)
    private Integer order; // 미디어 순서
}
