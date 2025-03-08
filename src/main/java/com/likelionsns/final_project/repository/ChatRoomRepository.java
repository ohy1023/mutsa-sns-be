package com.likelionsns.final_project.repository;

import com.likelionsns.final_project.domain.entity.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<Chat, Integer> {

    // 내가 만든 채팅방또는 내가 참여중인 채팅방을 전부 찾아주는 메서드
    @Query("select c from Chat c where c.createUser = :userName or c.joinUser = :userName")
    Slice<Chat> findChattingRoom(@Param("userName") String userName, Pageable pageable);

    @Query("select c from Chat c where (c.createUser = :myUserName and c.joinUser = :otherUserName) or (c.createUser = :otherUserName and c.joinUser = :myUserName)")
    Optional<Chat> findActiveChat(@Param("myUserName") String myUserName, @Param("otherUserName") String otherUserName);
}