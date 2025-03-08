package com.likelionsns.final_project.repository.mongo;

import com.likelionsns.final_project.domain.entity.mongo.Chatting;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoChatRepository extends MongoRepository<Chatting, String> {

    Slice<Chatting> findByChatRoomNo(Integer chatNo, Pageable pageable);
}