package com.api.localportfogram.comment.service;

import com.api.localportfogram.comment.dto.Comment;
import com.api.localportfogram.comment.dto.Comments;
import com.api.localportfogram.comment.entity.CommentEntity;
import com.api.localportfogram.comment.repository.CommentRepository;
import com.api.localportfogram.exception.dto.BadRequestException;
import com.api.localportfogram.exception.dto.ExceptionEnum;
import com.api.localportfogram.portfolio.entity.PortfolioEntity;
import com.api.localportfogram.portfolio.repository.PortfolioRepository;
import com.api.localportfogram.portfolio.service.PortfolioService;
import com.api.localportfogram.user.entity.UserEntity;
import com.api.localportfogram.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PortfolioService portfolioService;
    private final UserService userService;
    private final PortfolioRepository portfolioRepository;


    @Transactional(readOnly = true)
    public Comments getCommentsByPortfolioId(Long portfolioId, Pageable pageable) {
        List<Comment> commentList = commentRepository.findAllByPortfolioId(portfolioId, pageable)
                .stream()
                .map(Comment::fromEntity)
                .collect(Collectors.toList());

        if (commentList.isEmpty()) {
            // 댓글이 없는 것은 에러가 아님. 빈 리스트 반환
            return Comments.builder()
                    .comments(java.util.Collections.emptyList())
                    .totalPages(0)
                    .totalElements(0)
                    .total(0)
                    .build();
        }

        Comments comments = Comments.builder()
                .comments(commentList)
                .totalPages(commentList.size())
                .totalElements(commentList.size())
                .total(commentList.size())
                .build();

        return comments;
    }

    @Transactional(readOnly = true)
    public CommentEntity getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "댓글을 찾을 수 없습니다."));
    }

    @Transactional
    public Comment createComment(Comment commentDto) {
        if (commentDto.getContent() == null || commentDto.getContent().trim().isEmpty()) {
            throw new BadRequestException(ExceptionEnum.REQUEST_PARAMETER_INVALID, "댓글 내용을 입력해주세요.");
        }

        UserEntity user = userService.getMyUserWithAuthorities();
        PortfolioEntity portfolio = portfolioRepository.findById(commentDto.getPortfolioId())
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "포트폴리오를 찾을 수 없습니다."));

        CommentEntity commentEntity = CommentEntity.builder()
                .content(commentDto.getContent())
                .user(user)
                .portfolio(portfolio)
                .build();

        CommentEntity savedComment = commentRepository.save(commentEntity);
        return Comment.fromEntity(savedComment);
    }

    @Transactional
    public Comment updateComment(Long commentId, Comment comment) {

        if (comment == null || comment.getContent() == null) {
            throw new BadRequestException(ExceptionEnum.REQUEST_PARAMETER_INVALID, "댓글이 입력되지 않았습니다.");
        }
        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "댓글을 찾을 수 없습니다."));

        commentEntity.setContent(comment.getContent());
        CommentEntity updatedCommentEntity = commentRepository.save(commentEntity);

        Comment updatedComment = Comment.builder()
                .id(updatedCommentEntity.getId())
                .content(updatedCommentEntity.getContent())
                .build();

        return updatedComment;
    }

    @Transactional
    public void deleteComment(Long commentId) {
        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "댓글을 찾을 수 없습니다."));

        commentRepository.delete(commentEntity);
    }

}
