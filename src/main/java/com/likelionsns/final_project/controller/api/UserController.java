package com.likelionsns.final_project.controller.api;

import com.likelionsns.final_project.domain.dto.AlarmDto;
import com.likelionsns.final_project.domain.request.UpdateUserRequest;
import com.likelionsns.final_project.domain.request.UserLoginRequest;
import com.likelionsns.final_project.domain.response.*;
import com.likelionsns.final_project.domain.request.UserJoinRequest;
import com.likelionsns.final_project.service.AlarmService;
import com.likelionsns.final_project.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;

    private final AlarmService alarmService;

    @ApiOperation(value = "회원가입")
    @PostMapping("/join")
    public ResponseEntity<Response<UserJoinResponse>> joinUser(@RequestBody UserJoinRequest userJoinRequest) {
        UserJoinResponse userJoinResponse = userService.join(userJoinRequest);
        return ResponseEntity.ok().body(Response.success(userJoinResponse));
    }

    @ApiOperation(value = "로그인", notes = "jwt 반환")
    @PostMapping("/login")
    public ResponseEntity<Response<UserLoginResponse>> login(@RequestBody UserLoginRequest userLoginRequest) {
        String token = userService.login(userLoginRequest.getUserName(), userLoginRequest.getPassword());
        return ResponseEntity.ok().body(Response.success(new UserLoginResponse(token)));
    }

    @ApiOperation(value = "역할 변경")
    @PostMapping("/{userId}/role")
    public ResponseEntity<Response<UserRoleResponse>> changeRole(@PathVariable Integer userId, Authentication authentication) {
        UserRoleResponse response = userService.changeRole(userId, authentication.getName());
        return ResponseEntity.ok().body(Response.success(response));
    }


    @ApiOperation("알림 목록 조회")
    @GetMapping("/alarm")
    public ResponseEntity<Response<Page<AlarmDto>>> getAlarms(Authentication authentication, @PageableDefault(size = 20) @SortDefault(sort = "registeredAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AlarmDto> alarmDtos = alarmService.getAlarms(authentication.getName(), pageable);
        return ResponseEntity.ok().body(Response.success(alarmDtos));
    }

    @ApiOperation("회원 정보 수정")
    @PatchMapping
    public ResponseEntity<Void> changeUserInfo(@RequestPart MultipartFile multipartFile, UpdateUserRequest updateUserRequest, Authentication authentication) {
        userService.updateUserInfo(multipartFile, updateUserRequest, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @ApiOperation("팔로우 하기")
    @PostMapping("/follow/{targetUserId}")
    public ResponseEntity<Void> followUser(
            Authentication authentication, @PathVariable Integer targetUserId) {
        userService.followUser(authentication.getName(), targetUserId);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation("팔로우 취소")
    @DeleteMapping("/unfollow/{targetUserId}")
    public ResponseEntity<Void> unfollowUser(
            Authentication authentication, @PathVariable Integer targetUserId) {
        userService.unfollowUser(authentication.getName(), targetUserId);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation("내가 팔로우한 유저 수 조회")
    @GetMapping("/following/count")
    public ResponseEntity<Response<Long>> countFollowing(Authentication authentication) {
        return ResponseEntity.ok().body(Response.success(userService.countFollowing(authentication.getName())));
    }

    @ApiOperation("내가 팔로우한 유저 목록 조회")
    @GetMapping("/following")
    public ResponseEntity<Response<Page<UserInfoResponse>>> getFollowing(
            Authentication authentication, Pageable pageable) {
        return ResponseEntity.ok().body(Response.success(userService.getFollowingPage(authentication.getName(), pageable)));
    }

    @ApiOperation("나를 팔로우한 유저 수 조회")
    @GetMapping("/followers/count")
    public ResponseEntity<Response<Long>> countFollowers(Authentication authentication) {
        return ResponseEntity.ok().body(Response.success(userService.countFollowers(authentication.getName())));
    }

    @ApiOperation("나를 팔로우한 유저 목록 조회")
    @GetMapping("/followers")
    public ResponseEntity<Response<Page<UserInfoResponse>>> getFollowers(
            Authentication authentication, Pageable pageable) {
        return ResponseEntity.ok().body(Response.success(userService.getFollowersPage(authentication.getName(), pageable)));
    }

    @ApiOperation(value = "유저 검색", notes = "username 또는 nickname 기준으로 검색")
    @GetMapping("/search")
    public ResponseEntity<Response<Page<UserInfoResponse>>> searchUsers(
            @RequestParam String keyword,
            Pageable pageable
    ) {
        Page<UserInfoResponse> users = userService.searchUsers(keyword, pageable);
        return ResponseEntity.ok().body(Response.success(users));
    }


    // todo 해당 유저 정보 조회 (post, 팔로우 수, 팔로워 수 등)
//    @ApiOperation("특정 유저 정보 조회")
//    @GetMapping("/{userId}")
//    public ResponseEntity<Response<UserInfoResponse>> changeRole(@PathVariable Integer userId) {
//        return ResponseEntity.ok().body(Response.success(userService.getUserInfo(userId)));
//    }

}
