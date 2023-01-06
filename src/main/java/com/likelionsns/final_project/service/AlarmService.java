package com.likelionsns.final_project.service;

import com.likelionsns.final_project.domain.dto.AlarmDto;
import com.likelionsns.final_project.domain.entity.Alarm;
import com.likelionsns.final_project.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final AlarmRepository alarmRepository;

    public Page<AlarmDto> getAlarms(Pageable pageable) {
        Page<Alarm> alarms = alarmRepository.findAll(pageable);
        Page<AlarmDto> alarmDtos = alarms.map(alarm -> AlarmDto.builder()
                .id(alarm.getId())
                .alarmType(alarm.getAlarmType())
                .fromUserId(alarm.getFromUserId())
                .targetId(alarm.getTargetId())
                .text(alarm.getText())
                .createdAt(alarm.getRegisteredAt())
                .build());
        return alarmDtos;
    }
}
