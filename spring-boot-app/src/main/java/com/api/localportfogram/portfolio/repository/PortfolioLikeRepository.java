package com.api.localportfogram.portfolio.repository;

import com.api.localportfogram.portfolio.entity.PortfolioLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioLikeRepository extends JpaRepository<PortfolioLikeEntity,Long> {
    boolean existsByUserIdAndPortfolioId(@Param("userId")Long userId,@Param("portfolioId")Long portfolioId);
}

