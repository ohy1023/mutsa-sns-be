package com.likelionsns.final_project.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private String message;
    private Integer postId;

}
