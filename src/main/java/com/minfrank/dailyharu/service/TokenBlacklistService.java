package com.minfrank.dailyharu.service;

import com.minfrank.dailyharu.domain.TokenBlacklist;
import com.minfrank.dailyharu.repository.TokenBlacklistRepository;
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
public class TokenBlacklistService {
    private final TokenBlacklistRepository tokenBlacklistRepository;
    
    @Transactional
    public void blacklistToken(String token, long ttlMillis) {
        LocalDateTime expiryDate = LocalDateTime.now().plusNanos(TimeUnit.MILLISECONDS.toNanos(ttlMillis));
        
        TokenBlacklist tokenBlacklist = TokenBlacklist.builder()
            .token(token)
            .expiryDate(expiryDate)
            .build();
            
        tokenBlacklistRepository.save(tokenBlacklist);
        log.debug("블랙리스트 토큰 저장 완료: {}", token);
    }
    
    @Transactional(readOnly = true)
    public boolean isBlacklisted(String token) {
        return tokenBlacklistRepository.existsByToken(token);
    }
    
    @Transactional
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000) // 하루에 한 번
    public void cleanupExpiredTokens() {
        tokenBlacklistRepository.deleteAllExpiredTokens(LocalDateTime.now());
        log.debug("만료된 블랙리스트 토큰 정리 완료");
    }
} 