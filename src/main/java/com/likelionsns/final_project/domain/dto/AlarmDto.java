package com.likelionsns.final_project.domain.dto;

import com.likelionsns.final_project.domain.entity.Alarm;
import com.likelionsns.final_project.domain.entity.Comment;
import com.likelionsns.final_project.domain.enums.AlarmType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AlarmDto {
    private Integer id;
    private AlarmType alarmType;
    private Integer fromUserId;
    private Integer targetId;
    private String text;
    private LocalDateTime createdAt;

    @Builder
    public AlarmDto(Integer id, AlarmType alarmType, Integer fromUserId, Integer targetId, String text, LocalDateTime createdAt) {
        this.id = id;
        this.alarmType = alarmType;
        this.fromUserId = fromUserId;
        this.targetId = targetId;
        this.text = text;
        this.createdAt = createdAt;
    }

    public static Page<AlarmDto> toDtoList(Page<Alarm> alarms) {
        Page<AlarmDto> alarmDtos = alarms
                .map(alarm -> AlarmDto.builder()
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
