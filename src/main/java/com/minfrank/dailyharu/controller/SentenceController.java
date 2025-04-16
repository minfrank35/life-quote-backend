package com.minfrank.dailyharu.controller;

import com.minfrank.dailyharu.domain.User;
import com.minfrank.dailyharu.dto.SentenceRequest;
import com.minfrank.dailyharu.dto.SentenceResponse;
import com.minfrank.dailyharu.dto.TopSentencesResponse;
import com.minfrank.dailyharu.security.CustomUserDetails;
import com.minfrank.dailyharu.service.SentenceService;
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

@Tag(name = "문장 API", description = "문장 작성 및 조회 관련 API")
@RestController
@RequestMapping("/api/sentences")
@RequiredArgsConstructor
@Slf4j
public class SentenceController {
    private final SentenceService sentenceService;

    @Operation(summary = "문장 작성", description = "오늘의 문장을 작성합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<SentenceResponse> writeSentence(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid SentenceRequest request) {
        User user = userDetails.getUser();
        log.debug("문장 작성 요청: {}", user.getEmail());
        SentenceResponse response = sentenceService.writeSentence(user, request);
        log.debug("문장 작성 완료: {}", response.getId());
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "랜덤 문장 조회", description = "랜덤으로 문장을 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/random")
    public ResponseEntity<SentenceResponse> getRandomSentence(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        log.debug("랜덤 문장 조회 요청: {}", user.getEmail());
        SentenceResponse response = sentenceService.getRandomSentence(user.getId());
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "공감 추가", description = "문장에 공감을 추가합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{sentenceId}/empathy")
    public ResponseEntity<Void> addEmpathy(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long sentenceId) {
        User user = userDetails.getUser();
        sentenceService.addEmpathy(user.getId(), sentenceId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "공감 취소", description = "문장에 추가한 공감을 취소합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{sentenceId}/empathy")
    public ResponseEntity<Void> removeEmpathy(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long sentenceId) {
        User user = userDetails.getUser();
        sentenceService.removeEmpathy(user.getId(), sentenceId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "오늘 문장 작성 여부 확인", description = "오늘 문장을 작성했는지 확인합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/has-written-today")
    public ResponseEntity<Boolean> hasWrittenToday(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        log.debug("오늘 문장 작성 여부 확인 요청: {}", user.getEmail());
        boolean response = sentenceService.hasWrittenToday(user);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "오늘과 어제의 명언 조회", description = "오늘과 어제 작성된 문장 중 가장 많은 공감을 받은 문장을 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/top")
    public ResponseEntity<TopSentencesResponse> getTopSentences() {
        return ResponseEntity.ok(sentenceService.getTopSentences());
    }
} 