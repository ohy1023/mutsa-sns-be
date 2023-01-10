package com.likelionsns.final_project.controller;

import com.likelionsns.final_project.domain.dto.AlarmDto;
import com.likelionsns.final_project.domain.response.Response;
import com.likelionsns.final_project.service.AlarmService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/")
public class AlarmController {

    private final AlarmService alarmService;

    @ApiOperation("알림 목록 조회")
    @GetMapping("/alarm")
    public ResponseEntity<Response<Page<AlarmDto>>> getAlarms(Authentication authentication, @PageableDefault(size = 20) @SortDefault(sort = "registeredAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AlarmDto> alarmDtos = alarmService.getAlarms(authentication.getName(), pageable);
        return ResponseEntity.ok().body(Response.success(alarmDtos));
    }
}
