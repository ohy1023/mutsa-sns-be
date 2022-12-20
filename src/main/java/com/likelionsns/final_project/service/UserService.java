package com.likelionsns.final_project.service;

import com.likelionsns.final_project.domain.dto.UserJoinRequest;
import com.likelionsns.final_project.domain.dto.UserJoinResponse;
import com.likelionsns.final_project.domain.entity.User;
import com.likelionsns.final_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserJoinResponse join(UserJoinRequest userJoinRequest) {
        User savedUser = userRepository.save(userJoinRequest.toEntity());
        return UserJoinResponse.builder()
                .userId(savedUser.getId())
                .userName(savedUser.getUserName())
                .build();
    }

}
