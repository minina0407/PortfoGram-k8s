package com.api.localportfogram.reply.dto;

import com.api.localportfogram.reply.entity.ReplyEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Reply implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long userId;
    private Long commentId;
    private String content;
    private LocalDateTime createdAt;
    private String nickname;

    @Builder
    public Reply(Long id, Long userId, Long commentId, String content, LocalDateTime createdAt,String nickname) {
        this.id = id;
        this.userId = userId;
        this.commentId = commentId;
        this.content = content;
        this.createdAt = createdAt;
        this.nickname = nickname;
    }

    public static Reply fromEntity(ReplyEntity replyEntity) {

        return Reply.builder()
                .id(replyEntity.getId())
                .content(replyEntity.getContent())
                .nickname(replyEntity.getUser().getNickname())
                .build();
    }
}