package com.likelionsns.final_project.repository;

import com.likelionsns.final_project.domain.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Integer> {

}
