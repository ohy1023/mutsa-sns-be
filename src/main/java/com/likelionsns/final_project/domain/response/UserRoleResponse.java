package com.likelionsns.final_project.domain.response;

import com.likelionsns.final_project.domain.entity.User;
import com.likelionsns.final_project.domain.enums.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRoleResponse {
    private UserRole userRole;

    @Builder
    public UserRoleResponse(UserRole userRole) {
        this.userRole = userRole;
    }

    public static UserRoleResponse toResponse(User user) {
        return UserRoleResponse.builder()
                .userRole(user.getUserRole())
                .build();
    }
}
