package com.likelionsns.final_project.repository;

import com.likelionsns.final_project.domain.entity.Alarm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlarmRepository extends JpaRepository<Alarm, Integer> {

    @Query("SELECT a FROM Alarm a WHERE a.fromUserId <> :user_id and a.targetId = :user_id")
    Page<Alarm> findAllByUser(@Param("user_id") Integer user_id, Pageable pageable);

}
