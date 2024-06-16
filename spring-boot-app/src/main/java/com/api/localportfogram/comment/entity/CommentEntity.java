package com.api.localportfogram.comment.entity;


import com.api.localportfogram.comment.dto.Comment;
import com.api.localportfogram.portfolio.entity.PortfolioEntity;
import com.api.localportfogram.reply.entity.ReplyEntity;
import com.api.localportfogram.user.entity.UserEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@NamedEntityGraph(name = "Comment.portfolio",
        attributeNodes = @NamedAttributeNode("portfolio"))
@Table(name = "comment")
@Getter
public class CommentEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private PortfolioEntity portfolio;

    @Column(nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;


    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<ReplyEntity> replies ;


    @Builder
    public CommentEntity(Long id, UserEntity user, PortfolioEntity portfolio, String content, Date createdAt, List<ReplyEntity>replies) {
        this.id = id;
        this.user = user;
        this.portfolio= portfolio;
        this.content = content;
        this.createdAt = createdAt;
        this.replies = replies;
    }
    public void setContent(String content){
        this.content = content;
    }

    public Comment fromEntity(CommentEntity commentEntity) {
        return Comment.builder()
                .id(commentEntity.getId())
                .portfolioId(commentEntity.getPortfolio().getId())
                .content(commentEntity.getContent())
                .createdAt(commentEntity.getCreatedAt())
                .build();
    }
}

