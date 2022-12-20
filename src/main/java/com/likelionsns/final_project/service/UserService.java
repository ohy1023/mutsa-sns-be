package com.likelionsns.final_project.service;

import com.likelionsns.final_project.domain.dto.UserJoinRequest;
import com.likelionsns.final_project.domain.dto.UserJoinResponse;
import com.likelionsns.final_project.domain.entity.User;
import com.likelionsns.final_project.exception.ErrorCode;
import com.likelionsns.final_project.exception.SnsAppException;
import com.likelionsns.final_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.likelionsns.final_project.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserJoinResponse join(UserJoinRequest userJoinRequest) {
        userRepository.findByUserName(userJoinRequest.getUserName())
                .ifPresent((user -> {
                    throw new SnsAppException(DUPLICATED_USER_NAME, String.format("%s는 이미 존재합니다.",user.getUserName()));
                }));
        User savedUser = userRepository.save(userJoinRequest.toEntity());
        return UserJoinResponse.builder()
                .userId(savedUser.getId())
                .userName(savedUser.getUserName())
                .build();
    }

}
