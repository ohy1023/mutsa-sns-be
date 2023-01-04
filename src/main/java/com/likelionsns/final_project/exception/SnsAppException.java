package com.likelionsns.final_project.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SnsAppException extends RuntimeException{

    private ErrorCode errorCode;
    private String message;

}