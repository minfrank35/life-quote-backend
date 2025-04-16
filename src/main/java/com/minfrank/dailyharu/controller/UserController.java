package com.minfrank.dailyharu.controller;

import com.minfrank.dailyharu.domain.User;
import com.minfrank.dailyharu.dto.SentenceResponse;
import com.minfrank.dailyharu.dto.UpdateProfileRequest;
import com.minfrank.dailyharu.dto.UserResponse;
import com.minfrank.dailyharu.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "사용자 API", description = "사용자 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    
    @Operation(summary = "사용자 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal User user) {
        log.debug("사용자 정보 조회 요청: {}", user.getEmail());
        UserResponse response = UserResponse.from(user);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "최근 문장 목록 조회", description = "최근 작성된 문장들을 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me/sentences/recent")
    public ResponseEntity<List<SentenceResponse>> getRecentSentences(
        @AuthenticationPrincipal User user) {
        log.debug("최근 문장 목록 조회 요청: {}", user.getEmail());
        List<SentenceResponse> response = userService.getRecentSentences();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "프로필 수정", description = "사용자의 프로필 정보를 수정합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/me")
    public ResponseEntity<Void> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid UpdateProfileRequest request) {
        userService.updateProfile(user.getId(), request);
        return ResponseEntity.ok().build();
    }
} 