package com.likelionsns.final_project.domain.entity;

import com.likelionsns.final_project.domain.enums.AlarmType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE alarm SET deleted_at = CURRENT_TIMESTAMP WHERE alarm_id = ?")
public class Alarm extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    private String fromUserName;

    private String targetUserName;

    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Alarm(Integer id, AlarmType alarmType, String fromUserName, String targetUserName, String text, User user) {
        this.id = id;
        this.alarmType = alarmType;
        this.fromUserName = fromUserName;
        this.targetUserName = targetUserName;
        this.text = text;
        this.user = user;
    }
}
