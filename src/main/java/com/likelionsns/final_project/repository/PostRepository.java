package com.likelionsns.final_project.repository;

import com.likelionsns.final_project.domain.dto.PostDto;
import com.likelionsns.final_project.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface PostRepository extends JpaRepository<Post, Integer> {
    Page<Post> findAllByUserId(Pageable pageable, Integer userId);

    @Query("select p from Post p order by p.registeredAt desc")
    Page<Post> getPosts(Pageable pageable);
}
