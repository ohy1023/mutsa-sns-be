package com.likelionsns.final_project.repository;

import com.likelionsns.final_project.domain.entity.Post;
import com.likelionsns.final_project.domain.entity.PostMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostMediaRepository extends JpaRepository<PostMedia, Integer> {

    @Query("SELECT pm.mediaUrl FROM PostMedia pm WHERE pm.post = :post ORDER BY pm.mediaOrder LIMIT 1")
    String findThumbnailUrl (@Param("post") Post post);

    List<PostMedia> findPostMediaByPost(Post post);
}
