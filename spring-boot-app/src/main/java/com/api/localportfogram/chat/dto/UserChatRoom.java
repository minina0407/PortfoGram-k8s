package com.api.localportfogram.chat.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserChatRoom implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long userId;
    private Long chatRoomId;

    public UserChatRoom(Long id, Long userId, Long chatRoomId) {
        this.id = id;
        this.userId = userId;
        this.chatRoomId = chatRoomId;
    }
}