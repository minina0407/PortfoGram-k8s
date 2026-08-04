package com.api.localportfogram.comment.repository;

import com.api.localportfogram.comment.entity.CommentEntity;
import com.api.localportfogram.portfolio.entity.PortfolioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository  extends JpaRepository<CommentEntity, Long> {
    Page<CommentEntity> findAllByPortfolio(@Param("portfolio") PortfolioEntity portfolio, Pageable pageable);
    @EntityGraph(value = "Comment.portfolio", type = EntityGraph.EntityGraphType.LOAD)
    List<CommentEntity> findAllByPortfolioId(Long portfolioId, Pageable pageable);
}
