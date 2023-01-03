package com.likelionsns.final_project.domain.response;

import com.likelionsns.final_project.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private ErrorCode errorCode;
    private String message;
}
