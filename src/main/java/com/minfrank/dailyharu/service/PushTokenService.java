package com.minfrank.dailyharu.service;

import com.minfrank.dailyharu.domain.PushToken;
import com.minfrank.dailyharu.domain.User;
import com.minfrank.dailyharu.dto.PushTokenRequest;
import com.minfrank.dailyharu.repository.PushTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PushTokenService {
    private final PushTokenRepository pushTokenRepository;

    @Transactional
    public void saveToken(User user, PushTokenRequest request) {
        // 기존 토큰 비활성화
        pushTokenRepository.findByUserAndToken(user, request.getToken())
            .ifPresent(PushToken::deactivate);

        // 새 토큰 저장
        PushToken pushToken = PushToken.create(user, request.getToken(), request.getDeviceType());
        pushTokenRepository.save(pushToken);
    }

    @Transactional
    public void removeAllTokens(User user) {
        pushTokenRepository.findByUserAndIsActive(user, true)
            .forEach(PushToken::deactivate);
    }
} 