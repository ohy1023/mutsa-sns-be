package com.likelionsns.final_project.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailResponse {
    private String username;     // 사용자 아이디
    private String nickname;     // 사용자 닉네임
    private long followingCount;
    private long followerCount;
}
