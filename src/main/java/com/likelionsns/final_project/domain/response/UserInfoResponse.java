package com.likelionsns.final_project.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    private Integer userId;
    private String userName;
    private String nickName;
    private String userImg;
}
