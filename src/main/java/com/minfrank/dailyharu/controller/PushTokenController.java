package com.minfrank.dailyharu.controller;

import com.minfrank.dailyharu.domain.User;
import com.minfrank.dailyharu.dto.PushTokenRequest;
import com.minfrank.dailyharu.service.PushTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "푸시 토큰 API", description = "푸시 토큰 관리 관련 API")
@RestController
@RequestMapping("/api/push-tokens")
@RequiredArgsConstructor
public class PushTokenController {
    private final PushTokenService pushTokenService;

    @Operation(summary = "푸시 토큰 등록", description = "사용자의 푸시 토큰을 등록합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<Void> saveToken(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PushTokenRequest request) {
        pushTokenService.saveToken(user, request);
        return ResponseEntity.ok().build();
    }
} 