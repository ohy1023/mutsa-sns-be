package com.likelionsns.final_project.repository;

import com.likelionsns.final_project.domain.entity.Like;
import com.likelionsns.final_project.domain.entity.Post;
import com.likelionsns.final_project.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Integer> {

    Optional<Like> findByPostAndUser(Post post, User user);

    List<Like> findAllByPost(Post post);

    Long countByPost(Post post);
}

