package com.api.localportfogram.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class ChatMessageWorker {

    @RabbitListener(queues = RabbitMQConfig.CHAT_QUEUE)
    public void handleChatMessage(Map<String, Object> message) {
        log.info("Chat message received from queue: roomId={}, senderId={}",
                message.get("roomId"), message.get("senderId"));
    }
}
