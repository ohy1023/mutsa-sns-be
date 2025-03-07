package com.likelionsns.final_project.controller.api;

import com.likelionsns.final_project.domain.dto.AlarmDto;
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
        return ResponseEntity.ok().body(Response.success(alarmService.getAlarms(authentication.getName(), pageable)));
    }

    @ApiOperation("회원 정보 수정")
    @PatchMapping("/info")
    public ResponseEntity<Void> changeUserInfo(@RequestPart(required = false) MultipartFile multipartFile, @RequestPart(required = false) String newNickName, Authentication authentication) {
        userService.updateUserInfo(multipartFile, newNickName, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @ApiOperation("팔로우 하기")
    @PostMapping("/follow/{targetUserName}")
    public ResponseEntity<Void> followUser(
            Authentication authentication, @PathVariable String targetUserName) {
        userService.followUser(authentication.getName(), targetUserName);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation("팔로우 취소")
    @DeleteMapping("/unfollow/{targetUserName}")
    public ResponseEntity<Void> unfollowUser(
            Authentication authentication, @PathVariable String targetUserName) {
        userService.unfollowUser(authentication.getName(), targetUserName);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation("내가 팔로우한 유저 수 조회")
    @GetMapping("/my-following/count")
    public ResponseEntity<Response<Long>> countFollowing(Authentication authentication) {
        return ResponseEntity.ok().body(Response.success(userService.countFollowing(authentication.getName())));
    }

    @ApiOperation("내가 팔로우한 유저 목록 조회")
    @GetMapping("/my-following")
    public ResponseEntity<Response<Page<UserInfoResponse>>> getFollowing(
            Authentication authentication, Pageable pageable) {
        return ResponseEntity.ok().body(Response.success(userService.getFollowingPage(authentication.getName(), pageable)));
    }

    @ApiOperation("나를 팔로우한 유저 수 조회")
    @GetMapping("/my-followers/count")
    public ResponseEntity<Response<Long>> countFollowers(Authentication authentication) {
        return ResponseEntity.ok().body(Response.success(userService.countFollowers(authentication.getName())));
    }

    @ApiOperation("나를 팔로우한 유저 목록 조회")
    @GetMapping("/my-followers")
    public ResponseEntity<Response<Page<UserInfoResponse>>> getFollowers(
            Authentication authentication, Pageable pageable) {
        return ResponseEntity.ok().body(Response.success(userService.getFollowersPage(authentication.getName(), pageable)));
    }

    @ApiOperation("해당 유저 팔로우 유무")
    @GetMapping("/follow-check/{targetUserName}")
    public ResponseEntity<Boolean> followCheck(
            Authentication authentication, @PathVariable String targetUserName) {
        return ResponseEntity.ok().body(userService.followCheck(authentication.getName(), targetUserName));
    }

    @ApiOperation("특정 유저가 팔로우한 유저 목록 조회")
    @GetMapping("/{userName}/following")
    public ResponseEntity<Response<Page<UserInfoResponse>>> getFollowingByUserId(
            @PathVariable String userName,
            Pageable pageable
    ) {
        return ResponseEntity.ok().body(Response.success(userService.getFollowingPage(userName, pageable)));
    }

    @ApiOperation("특정 유저를 팔로우한 유저 목록 조회")
    @GetMapping("/{userName}/followers")
    public ResponseEntity<Response<Page<UserInfoResponse>>> getFollowersByUserId(
            @PathVariable String userName,
            Pageable pageable
    ) {
        return ResponseEntity.ok().body(Response.success(userService.getFollowersPage(userName, pageable)));
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

    @ApiOperation("특정 유저 정보 조회")
    @GetMapping("/{userName}")
    public ResponseEntity<Response<UserDetailResponse>> getUserInfo(@PathVariable String userName) {
        return ResponseEntity.ok().body(Response.success(userService.getUserInfo(userName)));
    }

    @ApiOperation("유저 정보 조회")
    @GetMapping("/info")
    public ResponseEntity<Response<UserDetailResponse>> getMyUserInfo(Authentication authentication) {
        return ResponseEntity.ok().body(Response.success(userService.getMyUserInfo(authentication.getName())));
    }


}
