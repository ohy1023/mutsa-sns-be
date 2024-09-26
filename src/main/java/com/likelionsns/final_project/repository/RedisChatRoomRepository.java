package com.likelionsns.final_project.repository;

import com.likelionsns.final_project.domain.dto.ChatRoom;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RedisChatRoomRepository extends CrudRepository<ChatRoom, String> {

    List<ChatRoom> findByChatroomNo(Integer chatRoomNo);

    Optional<ChatRoom> findByChatroomNoAndUserName(Integer chatRoomNo, String userName);
}