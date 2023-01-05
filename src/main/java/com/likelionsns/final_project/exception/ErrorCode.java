package com.likelionsns.final_project.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    DUPLICATED_USER_NAME(HttpStatus.CONFLICT, "UserName이 중복됩니다."),
    USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 UserName이 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "패스워드가 잘못되었습니다."),

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 토큰입니다."),
    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "사용자가 권한이 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 포스트가 없습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB에러"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 코맨트가 없습니다"),
    DUPLICATED_LIKE_COUNT(HttpStatus.CONFLICT,"이미 좋아요를 눌렀습니다."),
    ;

    private HttpStatus status;
    private String message;
}