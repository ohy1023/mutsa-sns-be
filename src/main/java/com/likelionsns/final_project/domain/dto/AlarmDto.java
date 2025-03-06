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
    private String fromUserName;
    private String targetUserName;
    private String text;
    private LocalDateTime registeredAt;


    @Builder
    public AlarmDto(Integer id, AlarmType alarmType, String fromUserName, String targetUserName, String text, LocalDateTime registeredAt) {
        this.id = id;
        this.alarmType = alarmType;
        this.fromUserName = fromUserName;
        this.targetUserName = targetUserName;
        this.text = text;
        this.registeredAt = registeredAt;
    }

    public static Page<AlarmDto> toDtoList(Page<Alarm> alarms) {
        return alarms
                .map(alarm -> AlarmDto.builder()
                        .id(alarm.getId())
                        .alarmType(alarm.getAlarmType())
                        .fromUserName(alarm.getFromUserName())
                        .targetUserName(alarm.getTargetUserName())
                        .text(alarm.getText())
                        .registeredAt(alarm.getRegisteredAt())
                        .build());
    }
}
