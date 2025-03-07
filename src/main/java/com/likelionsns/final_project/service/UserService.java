package com.likelionsns.final_project.service;

import com.likelionsns.final_project.domain.dto.UserDto;
import com.likelionsns.final_project.domain.entity.Follow;
import com.likelionsns.final_project.domain.request.UserJoinRequest;
import com.likelionsns.final_project.domain.response.UserDetailResponse;
import com.likelionsns.final_project.domain.response.UserInfoResponse;
import com.likelionsns.final_project.domain.response.UserJoinResponse;
import com.likelionsns.final_project.domain.entity.User;
import com.likelionsns.final_project.domain.response.UserRoleResponse;
import com.likelionsns.final_project.exception.SnsAppException;
import com.likelionsns.final_project.repository.FollowRepository;
import com.likelionsns.final_project.repository.UserRepository;
import com.likelionsns.final_project.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static com.likelionsns.final_project.domain.enums.UserRole.*;
import static com.likelionsns.final_project.domain.enums.UserRole.ADMIN;
import static com.likelionsns.final_project.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final AwsS3Service awsS3Service;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret}")
    private String secretKey;

    public UserJoinResponse join(UserJoinRequest userJoinRequest) {
        userRepository.findByUserName(userJoinRequest.getUserName())
                .ifPresent((user -> {
                    throw new SnsAppException(DUPLICATED_USER_NAME, DUPLICATED_USER_NAME.getMessage());
                }));

        userRepository.findByNickName(userJoinRequest.getNickName())
                .ifPresent((user -> {
                    throw new SnsAppException(DUPLICATED_NICK_NAME, DUPLICATED_NICK_NAME.getMessage());
                }));

        User savedUser = userRepository.save(userJoinRequest.toEntity(encoder.encode(userJoinRequest.getPassword())));
        return UserJoinResponse.toResponse(savedUser);
    }

    public String login(String userName, String password) {
        User user = findUserByUserName(userName);

        if (isWrongPassword(password, user))
            throw new SnsAppException(INVALID_PASSWORD, INVALID_PASSWORD.getMessage());

        long expiredTimeMs = 1000 * 60 * 60L;
        return JwtUtils.createToken(userName, secretKey, expiredTimeMs);
    }

    public UserRoleResponse changeRole(Integer userId, String userName) {
        User admin = findUserByUserName(userName);

        User targetUser = userRepository.findById(userId).orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        if (admin.getUserRole() != ADMIN) {
            throw new SnsAppException(INVALID_PERMISSION, INVALID_PERMISSION.getMessage());
        }

        if (targetUser.getUserRole() == USER) targetUser.promoteRole(targetUser);
        else if (targetUser.getUserRole() == ADMIN) targetUser.demoteRole(targetUser);

        return UserRoleResponse.toResponse(targetUser);
    }

    public void updateUserInfo(MultipartFile multipartFile, String newNickName, String userName) {
        User user = findUserByUserName(userName);

        if (multipartFile != null && !multipartFile.isEmpty()) {
            String userProfileImg = awsS3Service.uploadUserOriginImage(multipartFile);
            user.updateImg(userProfileImg);
        }

        if (newNickName != null && !newNickName.isEmpty()) {
            newNickName = URLDecoder.decode(newNickName, StandardCharsets.UTF_8);
            if (!user.getNickName().equals(newNickName)) {
                user.updateNickName(newNickName);
            }
        }
    }

    public void followUser(String userName, String targetUserName) {
        User follower = findUserByUserName(userName);
        User following = findUserByUserName(targetUserName);

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new SnsAppException(DUPLICATE_FOLLOW, DUPLICATE_FOLLOW.getMessage());
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        follower.addFollowing(follow);
        following.addFollower(follow);

        followRepository.save(follow);
    }

    public void unfollowUser(String userName, String targetUserName) {
        User follower = findUserByUserName(userName);
        User following = findUserByUserName(targetUserName);

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new SnsAppException(FOLLOW_NOT_FOUND, FOLLOW_NOT_FOUND.getMessage()));

        follower.removeFollowing(follow);
        following.removeFollower(follow);

        followRepository.delete(follow);
    }

    public boolean followCheck(String userName, String targetUserName) {
        User user = findUserByUserName(userName);
        User targetUser = findUserByUserName(targetUserName);

        return followRepository.existsByFollowerAndFollowing(user, targetUser);
    }

    @Transactional(readOnly = true)
    public long countFollowing(String userName) {
        return followRepository.countByFollower(findUserByUserName(userName));
    }

    @Transactional(readOnly = true)
    public long countFollowers(String userName) {
        return followRepository.countByFollowing(findUserByUserName(userName));
    }

    @Transactional(readOnly = true)
    public Page<UserInfoResponse> getFollowingPage(String userName, Pageable pageable) {
        return followRepository.findByFollower(findUserByUserName(userName), pageable).map(follow -> new UserInfoResponse(
                follow.getFollowing().getId(),
                follow.getFollowing().getUserName(),
                follow.getFollowing().getNickName(),
                follow.getFollowing().getUserImg()
        ));
    }

    @Transactional(readOnly = true)
    public Page<UserInfoResponse> getFollowersPage(String userName, Pageable pageable) {
        return followRepository.findByFollowing(findUserByUserName(userName), pageable).map(follow -> new UserInfoResponse(
                follow.getFollowing().getId(),
                follow.getFollowing().getUserName(),
                follow.getFollowing().getNickName(),
                follow.getFollowing().getUserImg()
        ));
    }

    @Transactional(readOnly = true)
    public Page<UserInfoResponse> searchUsers(String keyword, Pageable pageable) {
        return userRepository.searchUsers(keyword, pageable).map(user -> new UserInfoResponse(
                user.getId(),
                user.getUserName(),
                user.getNickName(),
                user.getUserImg()
        ));
    }

    @Transactional(readOnly = true)
    public UserDto getUserByUserName(String userName) {
        return UserDto.toUserDto(findUserByUserName(userName));
    }

    @Transactional(readOnly = true)
    public UserDetailResponse getUserInfo(String userName) {
        User user = findUserByUserName(userName);
        long followingCount = followRepository.countByFollower(user);
        long followerCount = followRepository.countByFollowing(user);

        return UserDetailResponse.builder()
                .userName(user.getUserName())
                .nickName(user.getNickName())
                .userImg(user.getUserImg())
                .followingCount(followingCount)
                .followerCount(followerCount)
                .build();
    }

    @Transactional(readOnly = true)
    public UserDetailResponse getMyUserInfo(String userName) {
        User user = findUserByUserName(userName);
        long followingCount = followRepository.countByFollower(user);
        long followerCount = followRepository.countByFollowing(user);

        return UserDetailResponse.builder()
                .userName(user.getUserName())
                .nickName(user.getNickName())
                .userImg(user.getUserImg())
                .followingCount(followingCount)
                .followerCount(followerCount)
                .build();
    }

    private User findUserByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));
    }

    private boolean isWrongPassword(String password, User user) {
        return !encoder.matches(password, user.getPassword());
    }

}
