package com.likelionsns.final_project.domain.dto;

import com.likelionsns.final_project.domain.entity.User;
import com.likelionsns.final_project.domain.enums.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserDto {
    private Integer id;
    private String userName;
    private String password;
    private UserRole userRole;

    @Builder
    public UserDto(Integer id, String userName, String password, UserRole userRole) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.userRole = userRole;
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .password(user.getPassword())
                .userRole(user.getUserRole())
                .build();
    }
}
