package com.likelionsns.final_project.repository;

import com.likelionsns.final_project.domain.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Integer> {
}

