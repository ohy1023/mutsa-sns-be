package com.likelionsns.final_project.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostSummaryInfoResponse {
    private Integer postId;
    private String postThumbnailUrl;
    private LocalDateTime registeredAt;
}
