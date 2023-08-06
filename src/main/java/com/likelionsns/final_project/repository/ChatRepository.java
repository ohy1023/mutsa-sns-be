package com.likelionsns.final_project.repository;

import com.likelionsns.final_project.domain.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Integer> {

    // 내가 만든 채팅방또는 내가 참여중인 채팅방을 전부 찾아주는 메서드
    @Query("select c from Chat c where c.createUser = :userId or c.joinUser = :userId")
    List<Chat> findChattingRoom(@Param("userId") Integer userId);

    @Query("select c from Chat c where (c.createUser = :myId and c.joinUser = :otherId) or (c.createUser = :otherId and c.joinUser = :myId)")
    Optional<Chat> findActiveChat(@Param("myId") Integer myId, @Param("otherId") Integer otherId);
}