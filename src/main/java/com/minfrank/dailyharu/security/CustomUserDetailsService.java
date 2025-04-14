package com.minfrank.dailyharu.security;

import com.minfrank.dailyharu.domain.Role;
import com.minfrank.dailyharu.domain.User;
import com.minfrank.dailyharu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("로그인 시도: {}", username);
        // username은 이메일로 사용됨
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> {
                log.error("사용자를 찾을 수 없습니다: {}", username);
                return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
            });
        
        // role이 null인 경우 기본값으로 USER 사용
        Role role = user.getRole() != null ? user.getRole() : Role.USER;
        
        log.info("사용자 인증 성공: {}", user.getEmail());
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(), // 이메일을 username으로 사용
            user.getPassword(),
            Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role.name()))
        );
    }
} 