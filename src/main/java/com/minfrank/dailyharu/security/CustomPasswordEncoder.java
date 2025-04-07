package com.minfrank.dailyharu.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 클라이언트 측에서 이미 해시된 비밀번호를 처리하기 위한 맞춤형 PasswordEncoder
 * 클라이언트가 SHA-256으로 해시한 비밀번호를 그대로 저장하고 비교합니다.
 */
@Component
public class CustomPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        // 이미 해시된 비밀번호가 전달되므로 추가 해싱 없이 그대로 사용
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // 이미 해시된 비밀번호를 문자열 비교로 검증
        return rawPassword.toString().equals(encodedPassword);
    }
} 