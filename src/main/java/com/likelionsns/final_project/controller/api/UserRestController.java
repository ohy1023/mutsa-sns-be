package com.likelionsns.final_project.controller.api;

import com.likelionsns.final_project.domain.dto.AlarmDto;
import com.likelionsns.final_project.domain.request.UserLoginRequest;
import com.likelionsns.final_project.domain.response.Response;
import com.likelionsns.final_project.domain.request.UserJoinRequest;
import com.likelionsns.final_project.domain.response.UserJoinResponse;
import com.likelionsns.final_project.domain.response.UserLoginResponse;
import com.likelionsns.final_project.domain.response.UserRoleResponse;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserRestController {
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
        log.info(userLoginRequest.getUserName());
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

}
