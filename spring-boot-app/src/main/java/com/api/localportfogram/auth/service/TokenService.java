package com.api.localportfogram.auth.service;


import com.api.localportfogram.auth.dto.Token;
import com.api.localportfogram.auth.utils.JwtTokenProvider;
import com.api.localportfogram.exception.dto.BadRequestException;
import com.api.localportfogram.user.dto.AuthorizeUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final StringRedisTemplate stringRedisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refreshToken:";

    public Token login(AuthorizeUser user) {

        UsernamePasswordAuthenticationToken authenticationToken = user.toAuthentication();
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String accessToken = jwtTokenProvider.createAccessToken(authentication, authorities);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication, authorities);

        Token token = Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        stringRedisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + authentication.getName(),
                token.getRefreshToken(), 7, TimeUnit.DAYS);
        return token;
    }
    public Token reissueToken(Token reissue) {
        // 1. Refresh Token 검증
        if (!jwtTokenProvider.validateRefreshToken(reissue.getRefreshToken())) {
            throw new BadRequestException("Refresh Token 정보가 유효하지 않습니다.");
        }

        // 2. Access Token에서 User email 가져옴
        Authentication authentication = jwtTokenProvider.getAuthentication(reissue.getAccessToken());

        // 3. Redis에서 User email을 기반으로 저장된 Refresh Token 값을 가져옴
        String refreshToken = stringRedisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + authentication.getName());
        if (!refreshToken.equals(reissue.getRefreshToken())) {
            throw new BadRequestException("Refresh Token 정보가 일치하지 않습니다.");
        }

        // 4. 새로운 토큰 생성
        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String newAccessToken = jwtTokenProvider.createAccessToken(authentication, authorities);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(authentication, authorities);

        Token tokenInfo = Token.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

        // 5. RefreshToken Redis 업데이트
        stringRedisTemplate.opsForValue()
                .set(REFRESH_TOKEN_PREFIX + authentication.getName(), tokenInfo.getRefreshToken(), Duration.ofDays(7));

        return tokenInfo;
    }

}
