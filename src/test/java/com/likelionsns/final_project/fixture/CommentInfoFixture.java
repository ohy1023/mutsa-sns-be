package com.likelionsns.final_project.fixture;

import com.likelionsns.final_project.domain.entity.Comment;

public class CommentInfoFixture {

    public static Comment get(String userName, String password) {
        return Comment.builder()
                .id(1)
                .comment("comment")
                .post(PostInfoFixture.get(userName, password))
                .user(UserInfoFixture.get(userName, password))
                .build();
    }
}
