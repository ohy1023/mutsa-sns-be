package com.likelionsns.final_project.service;

import com.likelionsns.final_project.domain.dto.AlarmDto;
import com.likelionsns.final_project.domain.entity.Alarm;
import com.likelionsns.final_project.domain.entity.User;
import com.likelionsns.final_project.exception.SnsAppException;
import com.likelionsns.final_project.repository.AlarmRepository;
import com.likelionsns.final_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.likelionsns.final_project.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;

    public Page<AlarmDto> getAlarms(String userName,Pageable pageable) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));
        Page<Alarm> alarms = alarmRepository.findAllByUser(user.getId(),pageable);
        Page<AlarmDto> alarmDtos = AlarmDto.toDtoList(alarms);
        return alarmDtos;
    }
}
