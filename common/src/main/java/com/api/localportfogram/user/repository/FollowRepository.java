package com.api.localportfogram.user.repository;

import com.api.localportfogram.user.entity.FollowEntity;
import com.api.localportfogram.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FollowRepository extends JpaRepository<FollowEntity, Long > {
    @Query("SELECT f.follower.id FROM FollowEntity f WHERE f.following.id = :id")
    Set<Long> findFollowerIdsById(@Param("id") Long userId);

    @Query("SELECT f.follower.id FROM FollowEntity f WHERE f.following.id = :id")
    List<Long> findByFollowingId(@Param("id") Long userId);

    Long countByFollowing(UserEntity user);

    Long countByFollower(UserEntity user);
}
