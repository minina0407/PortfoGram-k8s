package com.api.localportfogram.user.repository;

import com.api.localportfogram.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(@Param("email") String email);
    boolean existsByEmail(@Param("email") String email);
    boolean existsByNickname(@Param("nickname") String nickname);


}
