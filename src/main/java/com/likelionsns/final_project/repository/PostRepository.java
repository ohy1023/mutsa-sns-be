package com.likelionsns.final_project.repository;

import com.likelionsns.final_project.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface PostRepository extends JpaRepository<Post, Integer> {
    Page<Post> findAllByUserId(Integer userId, Pageable pageable);

    @Query("select p from Post p order by p.registeredAt desc")
    Page<Post> getPosts(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.user.id IN :userIds ORDER BY p.registeredAt DESC")
    Page<Post> findByUserIdIn(@Param("userIds") List<Integer> userIds, Pageable pageable);

}