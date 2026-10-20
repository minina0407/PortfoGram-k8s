package com.api.localportfogram.portfolio.service;

import com.api.localportfogram.Image.dto.Image;
import com.api.localportfogram.common.RedisConfig;
import com.api.localportfogram.portfolio.dto.Portfolio;
import com.api.localportfogram.portfolio.dto.PortfolioImage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 포트폴리오 상세 캐시(portfolio:{id})가 RedisConfig의 값 serializer를 거쳐
 * 저장 → 조회 → Portfolio 변환까지 되는지 확인한다. (Redis 없이 serializer만 사용)
 */
class PortfolioCacheSerializationTest {

    private final RedisConfig redisConfig = new RedisConfig();
    private final ObjectMapper objectMapper = redisConfig.objectMapper();

    @SuppressWarnings("unchecked")
    private final RedisSerializer<Object> valueSerializer =
            (RedisSerializer<Object>) redisConfig.redisTemplate(null).getValueSerializer();

    @Test
    void 캐시_적중_값은_LinkedHashMap으로_읽히고_Portfolio로_변환된다() {
        Portfolio original = Portfolio.builder()
                .id(1L)
                .userId(2L)
                .content("content")
                .createdAt(new Date(1_700_000_000_000L))
                .portfolioImages(List.of(PortfolioImage.builder()
                        .id(3L)
                        .image(Image.builder()
                                .imageId(4L)
                                .fileName("a.jpg")
                                .createdAt(LocalDateTime.of(2026, 10, 20, 12, 0))
                                .build())
                        .build()))
                .build();

        byte[] stored = valueSerializer.serialize(original);
        Object cached = valueSerializer.deserialize(stored);

        // 타입 정보 없이 저장되므로 그대로 캐스팅하면 ClassCastException (이슈 #8)
        assertThat(cached).isNotInstanceOf(Portfolio.class);

        Portfolio restored = objectMapper.convertValue(cached, Portfolio.class);

        assertThat(restored.getId()).isEqualTo(1L);
        assertThat(restored.getUserId()).isEqualTo(2L);
        assertThat(restored.getContent()).isEqualTo("content");
        assertThat(restored.getCreatedAt()).isEqualTo(new Date(1_700_000_000_000L));
        assertThat(restored.getPortfolioImages()).hasSize(1);
        assertThat(restored.getPortfolioImages().get(0).getImage().getCreatedAt())
                .isEqualTo(LocalDateTime.of(2026, 10, 20, 12, 0));
    }

    @Test
    void 수정_전_형식으로_저장된_캐시도_읽을_수_있다() {
        // 수정 전 serializer(기본 ObjectMapper)는 이미지 없는 포트폴리오만 저장할 수 있었고 Date를 숫자로 기록했다
        RedisSerializer<Object> legacySerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        Portfolio original = Portfolio.builder()
                .id(1L)
                .userId(2L)
                .content("content")
                .createdAt(new Date(1_700_000_000_000L))
                .portfolioImages(List.of())
                .build();

        byte[] stored = legacySerializer.serialize(original);
        Portfolio restored = objectMapper.convertValue(valueSerializer.deserialize(stored), Portfolio.class);

        assertThat(restored.getId()).isEqualTo(1L);
        assertThat(restored.getCreatedAt()).isEqualTo(new Date(1_700_000_000_000L));
        assertThat(restored.getPortfolioImages()).isEmpty();
    }
}
