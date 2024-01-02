package com.api.localportfogram.chat.repository;

import com.api.localportfogram.chat.entity.UserChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserChatRoomRepository extends JpaRepository<UserChatRoomEntity, Long> {
}
