package com.minfrank.dailyharu.controller;

import com.minfrank.dailyharu.domain.User;
import com.minfrank.dailyharu.dto.SentenceRequest;
import com.minfrank.dailyharu.dto.SentenceResponse;
import com.minfrank.dailyharu.repository.UserRepository;
import com.minfrank.dailyharu.service.SentenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sentences")
@RequiredArgsConstructor
@Slf4j
public class SentenceController {
    private final SentenceService sentenceService;
    private final UserRepository userRepository;
    
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
    
    @GetMapping("/random")
    public ResponseEntity<SentenceResponse> getRandomSentence(
        @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("랜덤 문장 조회 요청: {}", userDetails.getUsername());
        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        SentenceResponse response = sentenceService.getRandomSentence(user.getId());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{sentenceId}/empathy")
    public ResponseEntity<Void> addEmpathy(@PathVariable Long sentenceId) {
        sentenceService.addEmpathy(sentenceId);
        return ResponseEntity.ok().build();
    }
} 