package com.likelionsns.final_project.service;

import com.likelionsns.final_project.domain.dto.UserDto;
import com.likelionsns.final_project.domain.request.UserJoinRequest;
import com.likelionsns.final_project.domain.response.UserJoinResponse;
import com.likelionsns.final_project.domain.entity.User;
import com.likelionsns.final_project.domain.response.UserRoleResponse;
import com.likelionsns.final_project.exception.SnsAppException;
import com.likelionsns.final_project.repository.UserRepository;
import com.likelionsns.final_project.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.likelionsns.final_project.domain.enums.UserRole.*;
import static com.likelionsns.final_project.domain.enums.UserRole.ADMIN;
import static com.likelionsns.final_project.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret}")
    private String secretKey;

    @Transactional
    public UserJoinResponse join(UserJoinRequest userJoinRequest) {
        userRepository.findByUserName(userJoinRequest.getUsername())
                .ifPresent((user -> {
                    throw new SnsAppException(DUPLICATED_USER_NAME, DUPLICATED_USER_NAME.getMessage());
                }));
        User savedUser = userRepository.save(userJoinRequest.toEntity(encoder.encode(userJoinRequest.getPassword())));
        return UserJoinResponse.toResponse(savedUser);
    }

    @Transactional
    public String login(String userName, String password) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        if (isWrongPassword(password, user))
            throw new SnsAppException(INVALID_PASSWORD, INVALID_PASSWORD.getMessage());

        long expiredTimeMs = 1000 * 60 * 60L;
        return JwtUtils.createToken(userName, secretKey, expiredTimeMs);
    }

    @Transactional
    public UserRoleResponse changeRole(Integer userId, String username) {
        User admin = userRepository.findByUserName(username).orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        User targetUser = userRepository.findById(userId).orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        if (admin.getUserRole() != ADMIN) {
            throw new SnsAppException(INVALID_PERMISSION,INVALID_PERMISSION.getMessage());
        }

        if (targetUser.getUserRole() == USER) targetUser.promoteRole(targetUser);
        else if (targetUser.getUserRole() == ADMIN) targetUser.demoteRole(targetUser);

        return UserRoleResponse.toResponse(targetUser);
    }


    public UserDto getUserByUserName(String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));
        return UserDto.toUserDto(user);
    }

    private boolean isWrongPassword(String password, User user) {
        return !encoder.matches(password, user.getPassword());
    }

}
