package com.api.localportfogram.portfolio.dto;


import com.api.localportfogram.comment.dto.Comment;
import com.api.localportfogram.portfolio.entity.PortfolioEntity;
import com.api.localportfogram.reply.dto.Reply;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Portfolio implements Serializable {
    private static final long serialVersionUID = 12423L;
    private Long id;
    private Long userId;
    @NotBlank(message = "내용이 없습니다.")
    private String content;
    private Date createdAt;
    private List<PortfolioImage> portfolioImages;
    private List<Comment> comments;
    private List<Reply> replies;

    @Builder
    public Portfolio(Long id, Long userId, String content, Date createdAt, List<PortfolioImage> portfolioImages, List<Comment> comments, List<Reply> replies) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
        this.portfolioImages = portfolioImages;
        this.comments = comments;
        this.replies = replies;
    }

    public static Portfolio fromEntity(PortfolioEntity portfolioEntity) {
        List<PortfolioImage> portfolioImages= portfolioEntity.getPortfolioImages().stream()
                .map(PortfolioImage::fromEntity)
                .collect(Collectors.toList());

        List<Comment> comments = portfolioEntity.getComments().stream()
                .map(Comment::fromEntity)
                .collect(Collectors.toList());


        return Portfolio.builder()
                .id(portfolioEntity.getId())
                .content(portfolioEntity.getContent())
                .userId(portfolioEntity.getUser().getId())
                .portfolioImages(portfolioImages)
                .comments(comments)
                .build();
    }
}


