package com.minfrank.dailyharu.controller;

import com.minfrank.dailyharu.dto.EmailVerificationRequest;
import com.minfrank.dailyharu.dto.LoginRequest;
import com.minfrank.dailyharu.dto.RefreshTokenRequest;
import com.minfrank.dailyharu.dto.ResetPasswordRequest;
import com.minfrank.dailyharu.dto.SignupRequest;
import com.minfrank.dailyharu.dto.SignupResponse;
import com.minfrank.dailyharu.dto.TokenResponse;
import com.minfrank.dailyharu.service.EmailVerificationService;
import com.minfrank.dailyharu.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증 API", description = "회원가입, 로그인, 토큰 갱신 등의 인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final EmailVerificationService emailVerificationService;
    
    @Operation(summary = "이메일 인증 코드 전송", description = "이메일로 6자리 인증 코드를 전송합니다.")
    @PostMapping("/email/verify-request")
    public ResponseEntity<Void> requestEmailVerification(@RequestBody EmailVerificationRequest request) {
        emailVerificationService.sendVerificationCode(request.getEmail());
        return ResponseEntity.ok().build();
    }
    
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody @Valid SignupRequest request) {
        userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new SignupResponse("회원가입이 완료되었습니다."));
    }
    
    @Operation(summary = "이메일 인증", description = "이메일 인증 토큰을 검증합니다.")
    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        userService.verifyEmail(token);
        return ResponseEntity.ok().build();
    }
    
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        TokenResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "토큰 갱신", description = "리프레시 토큰으로 새로운 액세스 토큰을 발급받습니다.")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        TokenResponse response = userService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "로그아웃", description = "현재 사용자를 로그아웃합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            userService.logout(token.substring(7));
        }
        return ResponseEntity.ok().build();
    }
    
    @Operation(summary = "비밀번호 재설정 요청", description = "비밀번호 재설정 링크를 이메일로 전송합니다.")
    @PostMapping("/password/reset-request")
    public ResponseEntity<Void> requestPasswordReset(@RequestParam String email) {
        userService.requestPasswordReset(email);
        return ResponseEntity.ok().build();
    }
    
    @Operation(summary = "비밀번호 재설정", description = "비밀번호를 재설정합니다.")
    @PostMapping("/password/reset")
    public ResponseEntity<Void> resetPassword(
            @RequestParam String token,
            @RequestBody @Valid ResetPasswordRequest request) {
        userService.resetPassword(token, request.getNewPassword());
        return ResponseEntity.ok().build();
    }
} 