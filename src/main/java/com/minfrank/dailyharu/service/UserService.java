package com.minfrank.dailyharu.service;

import com.minfrank.dailyharu.domain.AuthProvider;
import com.minfrank.dailyharu.domain.EmailVerification;
import com.minfrank.dailyharu.domain.Role;
import com.minfrank.dailyharu.domain.User;
import com.minfrank.dailyharu.dto.LoginRequest;
import com.minfrank.dailyharu.dto.SignupRequest;
import com.minfrank.dailyharu.dto.TokenResponse;
import com.minfrank.dailyharu.dto.UpdateProfileRequest;
import com.minfrank.dailyharu.repository.EmailVerificationRepository;
import com.minfrank.dailyharu.repository.UserRepository;
import com.minfrank.dailyharu.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final EmailService emailService;
    private final RefreshTokenService tokenService;
    private final UserDetailsService userDetailsService;
    private final EmailVerificationService emailVerificationService;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    @Transactional
    public void signup(SignupRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        
        // 이메일 인증 코드 검증
        if (!emailVerificationService.verifyCode(request.getEmail(), request.getVerificationCode())) {
            throw new IllegalStateException("이메일 인증이 필요합니다.");
        }
        
        User user = User.builder()
            .email(request.getEmail())
            .username(request.getEmail())
            .password(request.getPassword()) // 클라이언트에서 이미 해시 처리되어 있으므로 그대로 저장
            .nickname(request.getNickname())
            .provider(AuthProvider.LOCAL)
            .emailVerified(true) // 인증 코드 검증이 완료되었으므로 이메일 인증 완료 처리
            .role(Role.USER) // 기본 역할 설정
            .build();
            
        userRepository.save(user);
    }
    
    @Transactional
    public void verifyEmail(String token) {
        EmailVerification verification = emailVerificationRepository.findByToken(token)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));
            
        if (verification.isVerified()) {
            throw new IllegalStateException("이미 인증된 이메일입니다.");
        }
        
        if (verification.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("만료된 토큰입니다.");
        }
        
        User user = userRepository.findByEmail(verification.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            
        user.verifyEmail();
        verification.verify();
    }
    
    @Transactional(readOnly = false)
    public TokenResponse login(LoginRequest request) {
        try {
            log.debug("로그인 시도: {}으로 로그인 요청", request.getEmail());
            // 로그인 처리 - 클라이언트에서 해시된 비밀번호 그대로 사용
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            log.debug("인증 성공, 토큰 생성 시작");
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(request.getEmail());
            
            log.debug("리프레시 토큰 저장");
            // 데이터베이스에 리프레시 토큰 저장
            tokenService.saveToken(
                "refresh_token:" + request.getEmail(),
                refreshToken,
                30,
                TimeUnit.DAYS
            );
            
            log.debug("TokenResponse 객체 생성 및 반환");
            User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            return new TokenResponse(jwt, refreshToken, user.getNickname());
        } catch (BadCredentialsException e) {
            log.error("인증 실패: 이메일 또는 비밀번호가 올바르지 않음", e);
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        } catch (Exception e) {
            log.error("로그인 처리 중 예상치 못한 오류 발생", e);
            throw new RuntimeException("로그인 처리 중 오류가 발생했습니다.", e);
        }
    }
    
    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            
        String token = UUID.randomUUID().toString();
        // 비밀번호 재설정 토큰 저장
        tokenService.saveToken(
            "pwd_reset:" + token,
            user.getEmail(),
            24,
            TimeUnit.HOURS
        );
        
        // 이메일 전송은 실제 구현 시 활성화
        // emailService.sendPasswordResetEmail(email, token);
    }
    
    @Transactional
    public void resetPassword(String token, String newPassword) {
        String email = tokenService.getToken("pwd_reset:" + token);
        
        if (email == null) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 토큰입니다.");
        }
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            
        user.updatePassword(newPassword);
        tokenService.removeToken("pwd_reset:" + token);
    }
    
    @Transactional
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            
        user.updateProfile(request.getNickname());
    }
    
    @Transactional
    public void logout(String token) {
        Claims claims = tokenProvider.getClaims(token);
        long expirationTime = claims.getExpiration().getTime() - System.currentTimeMillis();
        
        tokenBlacklistService.blacklistToken(token, expirationTime);
    }

    @Transactional(readOnly = false)
    public TokenResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }
        
        String email = tokenProvider.getUserEmailFromToken(refreshToken);
        String storedRefreshToken = tokenService.getToken("refresh_token:" + email);
        
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new IllegalArgumentException("만료되었거나 유효하지 않은 리프레시 토큰입니다.");
        }
        
        // 새 액세스 토큰 생성
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        }
        
        String newAccessToken = tokenProvider.generateToken(authentication);
        String newRefreshToken = tokenProvider.generateRefreshToken(email);
        
        // 새 리프레시 토큰으로 교체
        tokenService.saveToken(
            "refresh_token:" + email,
            newRefreshToken,
            30,
            TimeUnit.DAYS
        );
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return new TokenResponse(newAccessToken, newRefreshToken, user.getNickname());
    }
} 