package com.likelionsns.final_project.repository;

import com.likelionsns.final_project.domain.entity.Comment;
import com.likelionsns.final_project.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Transactional
    void deleteAllByPost(Post post);

    Page<Comment> findAllByPost(Post post, Pageable pageable);
}
