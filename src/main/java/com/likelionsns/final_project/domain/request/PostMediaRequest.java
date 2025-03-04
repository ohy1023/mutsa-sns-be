package com.likelionsns.final_project.domain.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostMediaRequest {
    private MultipartFile multipartFile;
    private Integer order;
}

