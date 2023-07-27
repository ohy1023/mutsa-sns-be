package com.likelionsns.final_project.domain.entity;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@DynamicInsert
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_no")
    private Integer chatNo;

    @Column(name = "create_user")
    private Integer createUser;

    @Column(name = "join_user")
    private Integer joinUser;

    @Column(name = "reg_date")
    private LocalDateTime regDate;


}