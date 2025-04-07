package com.minfrank.dailyharu.service;

import com.minfrank.dailyharu.domain.RefreshToken;
import com.minfrank.dailyharu.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    
    @Transactional
    public void saveToken(String key, String value, long ttl, TimeUnit timeUnit) {
        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(timeUnit.toSeconds(ttl));
        
        refreshTokenRepository.findByTokenKey(key)
            .ifPresentOrElse(
                // 토큰이 이미 존재하면 업데이트
                token -> token.updateToken(value, expiryDate),
                // 토큰이 없으면 새로 생성
                () -> {
                    RefreshToken refreshToken = RefreshToken.builder()
                        .tokenKey(key)
                        .tokenValue(value)
                        .expiryDate(expiryDate)
                        .build();
                    refreshTokenRepository.save(refreshToken);
                }
            );
        log.debug("토큰 저장 완료: {}", key);
    }
    
    @Transactional(readOnly = true)
    public String getToken(String key) {
        return refreshTokenRepository.findByTokenKey(key)
            .filter(token -> !token.isExpired())
            .map(RefreshToken::getTokenValue)
            .orElse(null);
    }
    
    @Transactional
    public void removeToken(String key) {
        refreshTokenRepository.deleteByTokenKey(key);
        log.debug("토큰 삭제 완료: {}", key);
    }
    
    @Transactional
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000) // 하루에 한 번
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteAllExpiredTokens(LocalDateTime.now());
        log.debug("만료된 리프레시 토큰 정리 완료");
    }
} 