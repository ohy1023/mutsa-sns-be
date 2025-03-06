package com.likelionsns.final_project.repository;

import com.likelionsns.final_project.domain.entity.Alarm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlarmRepository extends JpaRepository<Alarm, Integer> {

    @Query("SELECT a FROM Alarm a WHERE a.fromUserName <> :userName and a.targetUserName = :userName")
    Page<Alarm> findAllByUser(@Param("userName") String userName, Pageable pageable);

}
