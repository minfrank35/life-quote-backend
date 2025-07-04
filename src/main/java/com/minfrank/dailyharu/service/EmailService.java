package com.minfrank.dailyharu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender emailSender;
    
    public void sendVerificationEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("데일리하루 - 이메일 인증");
        message.setText("당신의 인증번호는 " + code + "입니다. 5분 이내에 입력해주세요.");
        
        emailSender.send(message);
    }
    
    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("데일리하루 - 비밀번호 재설정");
        message.setText("다음 링크를 클릭하여 비밀번호를 재설정해주세요: " +
            "http://localhost:8080/api/auth/reset-password?token=" + token);
        
        emailSender.send(message);
    }
} 