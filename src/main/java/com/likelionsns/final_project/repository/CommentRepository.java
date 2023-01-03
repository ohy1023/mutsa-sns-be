package com.likelionsns.final_project.repository;

import com.likelionsns.final_project.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
