package com.api.localportfogram.portfolio.repository;


import com.api.localportfogram.portfolio.entity.PortfolioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioImageRepository extends JpaRepository<PortfolioEntity, Long> {
}
