package com.api.localportfogram.batch.job;

import com.api.localportfogram.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PortfolioStatsJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final UserRepository userRepository;

    @Bean
    public Job portfolioStatsSummaryJob() {
        return new JobBuilder("portfolioStatsSummaryJob", jobRepository)
                .start(collectStatsStep())
                .build();
    }

    @Bean
    public Step collectStatsStep() {
        return new StepBuilder("collectStatsStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    long userCount = userRepository.count();
                    log.info("[batch] portfolioStatsSummaryJob completed - total users: {}", userCount);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
