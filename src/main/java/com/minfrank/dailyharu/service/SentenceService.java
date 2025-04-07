package com.minfrank.dailyharu.service;

import com.minfrank.dailyharu.domain.Sentence;
import com.minfrank.dailyharu.domain.User;
import com.minfrank.dailyharu.dto.SentenceRequest;
import com.minfrank.dailyharu.dto.SentenceResponse;
import com.minfrank.dailyharu.repository.SentenceRepository;
import com.minfrank.dailyharu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SentenceService {
    private final SentenceRepository sentenceRepository;
    private final UserRepository userRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Transactional
    public SentenceResponse writeSentence(Long userId, SentenceRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            
        LocalDate today = LocalDate.now();
        if (sentenceRepository.existsByUserAndWrittenDate(user, today)) {
            throw new IllegalStateException("오늘은 이미 문장을 작성했습니다.");
        }
        
        Sentence sentence = Sentence.builder()
            .user(user)
            .content(request.getContent())
            .writtenDate(today)
            .build();
            
        sentenceRepository.save(sentence);
        return SentenceResponse.success(sentence.getContent(), sentence.getWrittenDate().format(DATE_FORMATTER));
    }
    
    public SentenceResponse getRandomSentence(Long userId) {
        LocalDate today = LocalDate.now();
        Sentence randomSentence = sentenceRepository.findRandomSentenceExceptUser(userId, today)
            .orElseThrow(() -> new IllegalStateException("오늘 작성된 다른 문장이 없습니다."));
            
        return new SentenceResponse(
            randomSentence.getContent(),
            randomSentence.getWrittenDate().format(DATE_FORMATTER),
            null,
            null,
            randomSentence.getUser().getNickname()
        );
    }
    
    @Transactional
    public void addEmpathy(Long sentenceId) {
        Sentence sentence = sentenceRepository.findById(sentenceId)
            .orElseThrow(() -> new IllegalArgumentException("문장을 찾을 수 없습니다."));
        sentence.increaseEmpathyCount();
    }
} 