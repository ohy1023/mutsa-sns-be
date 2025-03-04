package com.likelionsns.final_project.repository;

import com.likelionsns.final_project.domain.entity.Follow;
import com.likelionsns.final_project.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowing(User follower, User following);

    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    Page<Follow> findByFollower(User follower, Pageable pageable); // 내가 팔로우한 유저들
    Page<Follow> findByFollowing(User following, Pageable pageable); // 나를 팔로우한 유저들

    long countByFollower(User follower); // 내가 팔로우한 수
    long countByFollowing(User following); // 나를 팔로우한 수
}
