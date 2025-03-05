package com.likelionsns.final_project.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    WRONG_FILE_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일입니다"),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),

    DUPLICATED_USER_NAME(HttpStatus.CONFLICT, "UserName이 중복됩니다."),
    DUPLICATED_NICK_NAME(HttpStatus.CONFLICT, "NickName이 중복됩니다."),
    USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 UserName이 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "패스워드가 잘못되었습니다."),

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 토큰입니다."),
    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "사용자가 권한이 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 포스트가 없습니다."),
    LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "업로드 가능 한도를 초과했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB에러"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 코맨트가 없습니다"),
    MEDIA_NOT_FOUND(HttpStatus.NOT_FOUND, "미디어가 없습니다."),
    MEDIA_COUNT_MISMATCH(HttpStatus.BAD_REQUEST, "파일 개수와 순서 개수가 일치하지 않습니다."),
    INVALID_MEDIA_ORDER_LIST(HttpStatus.CONFLICT, "Invalid media order list format."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요를 취소할려면 좋아요를 눌러주세요"),
    DUPLICATED_LIKE_COUNT(HttpStatus.CONFLICT, "이미 좋아요를 눌렀습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    DUPLICATE_FOLLOW(HttpStatus.CONFLICT, "이미 팔로우한 유저입니다."),
    FOLLOW_NOT_FOUND(HttpStatus.NOT_FOUND, "팔로우 관계가 존재하지 않습니다."),

    NOT_SAVE_TARGET(HttpStatus.BAD_REQUEST, "보낸 메세지만 저장됩니다."),
    ALREADY_CHAT_ROOM(HttpStatus.CONFLICT, "이미 채팅 방이 존재 합니다.");

    private HttpStatus status;
    private String message;
}