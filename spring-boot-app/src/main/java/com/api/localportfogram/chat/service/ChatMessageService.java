package com.api.localportfogram.chat.service;

import com.api.localportfogram.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE_NAME = "chat";
/*
    @Transactional
    public void saveChatMessage(Long chatRoomId, ChatMessage message, Long senderId){
        ChatRoomEntity chatRoom = chatRoomService.getChatRoomById(chatRoomId);
        Long receiverId = chatRoom.getSenderId().equals(senderId) ? chatRoom.getReceiverId() : chatRoom.getSenderId();
        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .chatRoom(chatRoom)
                .senderId(senderId)
                .receiverId(receiverId)
                .content(message.getContent())
                .createdAt(now())
                .build();
        chatMessageRepository.save(chatMessage);
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, "chat."+ chatRoomId, chatMessage.toEntity(chatMessage)); // toEntity()로 변환하여 보내기
        log.info("chatRoomId = {}", chatMessage.getChatRoom().getId());
    }
    public List<ChatMessage> getLastMessages(Long roomId) {
        // DB에서 최근 20개의 채팅 메시지를 가져옵니다.
        List<ChatMessageEntity> chatMessageEntities = chatMessageRepository.findTop20ByChatRoomIdOrderByCreatedAtDesc(roomId);

        // ChatMessageEntity를 ChatMessage로 변환합니다.
        List<ChatMessage> chatMessages = chatMessageEntities.stream()
                .map(ChatMessage::fromEntity)
                .collect(Collectors.toList());

        // 결과를 반환합니다.
        return chatMessages != null ? chatMessages : new ArrayList<>();
    }

    */
}
