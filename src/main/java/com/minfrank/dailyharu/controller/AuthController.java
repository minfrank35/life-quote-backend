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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final EmailVerificationService emailVerificationService;
    
    @PostMapping("/email/verify-request")
    public ResponseEntity<Void> requestEmailVerification(@RequestBody EmailVerificationRequest request) {
        emailVerificationService.sendVerificationCode(request.getEmail());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody @Valid SignupRequest request) {
        userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new SignupResponse("회원가입이 완료되었습니다."));
    }
    
    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        userService.verifyEmail(token);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        TokenResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        TokenResponse response = userService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            userService.logout(token.substring(7));
        }
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/password/reset-request")
    public ResponseEntity<Void> requestPasswordReset(@RequestParam String email) {
        userService.requestPasswordReset(email);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/password/reset")
    public ResponseEntity<Void> resetPassword(
            @RequestParam String token,
            @RequestBody @Valid ResetPasswordRequest request) {
        userService.resetPassword(token, request.getNewPassword());
        return ResponseEntity.ok().build();
    }
} 