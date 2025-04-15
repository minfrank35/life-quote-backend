package com.minfrank.dailyharu.controller;

import com.minfrank.dailyharu.domain.User;
import com.minfrank.dailyharu.dto.SentenceRequest;
import com.minfrank.dailyharu.dto.SentenceResponse;
import com.minfrank.dailyharu.repository.UserRepository;
import com.minfrank.dailyharu.service.SentenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "문장 API", description = "문장 작성 및 조회 관련 API")
@RestController
@RequestMapping("/api/sentences")
@RequiredArgsConstructor
@Slf4j
public class SentenceController {
    private final SentenceService sentenceService;
    private final UserRepository userRepository;
    
    @Operation(summary = "문장 작성", description = "오늘의 문장을 작성합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<SentenceResponse> writeSentence(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody @Valid SentenceRequest request) {
        log.debug("문장 작성 요청: {}", userDetails.getUsername());
        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        SentenceResponse response = sentenceService.writeSentence(user.getId(), request);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "랜덤 문장 조회", description = "랜덤으로 문장을 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/random")
    public ResponseEntity<SentenceResponse> getRandomSentence(
        @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("랜덤 문장 조회 요청: {}", userDetails.getUsername());
        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        SentenceResponse response = sentenceService.getRandomSentence(user.getId());
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "공감 추가", description = "문장에 공감을 추가합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{sentenceId}/empathy")
    public ResponseEntity<Void> addEmpathy(@PathVariable Long sentenceId) {
        sentenceService.addEmpathy(sentenceId);
        return ResponseEntity.ok().build();
    }
} 