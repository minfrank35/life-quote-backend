package com.minfrank.dailyharu.service;

import com.minfrank.dailyharu.domain.EmailVerification;
import com.minfrank.dailyharu.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {
    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailService emailService;
    private final Random random = new Random();
    
    // 이메일 인증 코드 발송
    @Transactional
    public void sendVerificationCode(String email) {
        String code = generateVerificationCode();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(5);
        
        // 기존 인증 코드가 있으면 삭제
        emailVerificationRepository.findByEmail(email)
            .ifPresent(verification -> emailVerificationRepository.delete(verification));
        
        // 새 인증 코드 저장
        EmailVerification verification = EmailVerification.builder()
            .email(email)
            .token(code)
            .expiryDate(expiryDate)
            .build();
            
        emailVerificationRepository.save(verification);
        emailService.sendVerificationEmail(email, code);
        
        log.info("이메일 인증 코드 발송: {}, 만료시간: {}", email, expiryDate);
    }
    
    // 이메일 인증 코드 검증
    @Transactional
    public boolean verifyCode(String email, String code) {
        EmailVerification verification = emailVerificationRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("인증 코드를 찾을 수 없습니다."));
        
        // 인증 코드 만료 여부 확인
        if (verification.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("인증 코드가 만료되었습니다.");
        }
        
        // 인증 코드 일치 여부 확인
        if (!verification.getToken().equals(code)) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }
        
        // 이미 인증된 코드인지 확인
        if (verification.isVerified()) {
            throw new IllegalStateException("이미 인증된 코드입니다.");
        }
        
        // 인증 성공 처리
        verification.verify();
        emailVerificationRepository.save(verification);
        log.info("이메일 인증 성공: {}", email);
        
        return true;
    }
    
    // 6자리 인증 코드 생성
    private String generateVerificationCode() {
        return String.format("%06d", random.nextInt(1000000));
    }
    
    // 만료된 인증 코드 정리 (매일 새벽 1시에 실행)
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void cleanupExpiredVerifications() {
        LocalDateTime now = LocalDateTime.now();
        int count = emailVerificationRepository.deleteAllByExpiryDateBefore(now);
        log.info("만료된 인증 코드 {} 개 삭제 완료", count);
    }
} 