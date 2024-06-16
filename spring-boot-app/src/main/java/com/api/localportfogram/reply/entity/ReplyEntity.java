package com.api.localportfogram.reply.entity;


import com.api.localportfogram.comment.entity.CommentEntity;
import com.api.localportfogram.reply.dto.Reply;
import com.api.localportfogram.user.entity.UserEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reply")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private CommentEntity comment;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    

    @Builder
    public ReplyEntity(Long id, UserEntity user, CommentEntity comment, String content, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.comment = comment;
        this.content = content;
        this.createdAt = createdAt;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public Reply fromEntity(ReplyEntity replyEntity) {
        return Reply.builder()
                .id(replyEntity.getId())
                .commentId(replyEntity.getComment().getId())
                .content(replyEntity.getContent())
                .createdAt(replyEntity.getCreatedAt())
                .build();
    }
}
