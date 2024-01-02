package com.api.localportfogram.portfolio.repository;


import com.api.localportfogram.portfolio.entity.PortfolioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioEntity, Long> {
    Optional<PortfolioEntity> findPortfolioEntityById(Long id);

    Page<PortfolioEntity> findByIdIn(List<Long> Ids, Pageable pageable);

}

