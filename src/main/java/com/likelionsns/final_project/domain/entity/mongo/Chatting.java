package com.likelionsns.final_project.domain.entity.mongo;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "chatting")
// MongoDB Chatting 모델
public class Chatting {

    @Id
    private String id;
    private Integer chatRoomNo;
    private String senderName;
    private String content;
    private LocalDateTime sendDate;
    private long readCount;

}
