package com.api.localportfogram.chat.repository;

import com.api.localportfogram.chat.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findTop20ByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);
}
