package com.likelionsns.final_project.repository;

import com.likelionsns.final_project.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    // 추가적인 메서드나 쿼리가 필요하면 여기에 선언합니다.
}