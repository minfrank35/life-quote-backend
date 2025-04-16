package com.minfrank.dailyharu.service;

import com.minfrank.dailyharu.domain.PushToken;
import com.minfrank.dailyharu.domain.Sentence;
import com.minfrank.dailyharu.repository.PushTokenRepository;
import com.minfrank.dailyharu.repository.SentenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationService {
    private final PushTokenRepository pushTokenRepository;
    private final SentenceRepository sentenceRepository;
    private final FCMService fcmService;

    @Scheduled(cron = "0 0 9 * * ?") // 매일 오전 9시
    @Transactional
    public void sendDailyTopSentenceNotification() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        
        // 어제의 최고 문장 조회
        Sentence topSentence = sentenceRepository.findTopByWrittenDateOrderByEmpathyCountDesc(yesterday)
            .orElse(null);
            
        if (topSentence == null) {
            log.info("어제 작성된 문장이 없습니다.");
            return;
        }
        
        // 활성화된 모든 푸시 토큰 조회
        List<PushToken> activeTokens = pushTokenRepository.findByIsActive(true);
        
        // 푸시 알림 전송
        for (PushToken token : activeTokens) {
            try {
                fcmService.sendPushNotification(
                    token.getToken(),
                    "어제의 최고 명언",
                    topSentence.getContent()
                );
            } catch (Exception e) {
                log.error("푸시 알림 전송 실패: {}", token.getToken(), e);
            }
        }
    }
} 