package com.likelionsns.final_project.service;

import com.likelionsns.final_project.domain.dto.UserDto;
import com.likelionsns.final_project.domain.request.UserJoinRequest;
import com.likelionsns.final_project.domain.response.UserJoinResponse;
import com.likelionsns.final_project.domain.entity.User;
import com.likelionsns.final_project.exception.ErrorCode;
import com.likelionsns.final_project.exception.SnsAppException;
import com.likelionsns.final_project.repository.UserRepository;
import com.likelionsns.final_project.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.likelionsns.final_project.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret}")
    private String secretKey;

    private final long expiredTimeMs = 1000 * 60 * 60L;


    public UserJoinResponse join(UserJoinRequest userJoinRequest) {
        userRepository.findByUserName(userJoinRequest.getUserName())
                .ifPresent((user -> {
                    throw new SnsAppException(DUPLICATED_USER_NAME, String.format("%s는 이미 존재합니다.",user.getUserName()));
                }));
        User savedUser = userRepository.save(userJoinRequest.toEntity(encoder.encode(userJoinRequest.getPassword())));
        return UserJoinResponse.builder()
                .userId(savedUser.getId())
                .userName(savedUser.getUserName())
                .build();
    }
    public String login(String userName, String password) {

        //userName 있는지 여부 확인
        //없으면 NOT_FOUND 에러 발생
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(ErrorCode.USER_NOT_FOUND, String.format("%s는 존재하지 않습니다.", userName)));

        //password 일치 하는지 여부 확인
        if(!encoder.matches(password, user.getPassword()))
            throw new SnsAppException(ErrorCode.INVALID_PASSWORD, String.format("id 또는 password 가 틀렸습니다."));

        return JwtUtils.createToken(userName, secretKey, expiredTimeMs);
    }



    public UserDto getUserByUserName(String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USER_NOT_FOUND, userName + "이 없습니다."));
        return UserDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .password(user.getPassword())
                .userRole(user.getUserRole())
                .build();
    }




}
