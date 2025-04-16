package com.minfrank.dailyharu.service;

import com.minfrank.dailyharu.domain.Sentence;
import com.minfrank.dailyharu.domain.User;
import com.minfrank.dailyharu.dto.SentenceRequest;
import com.minfrank.dailyharu.dto.SentenceResponse;
import com.minfrank.dailyharu.dto.TopSentencesResponse;
import com.minfrank.dailyharu.repository.SentenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SentenceService {
    private final SentenceRepository sentenceRepository;

    @Transactional
    public SentenceResponse writeSentence(User user, SentenceRequest request) {
        if (hasWrittenToday(user)) {
            throw new IllegalStateException("이미 오늘 문장을 작성했습니다.");
        }

        Sentence sentence = Sentence.builder()
                .user(user)
                .content(request.getContent())
                .writtenDate(LocalDate.now())
                .build();

        sentence = sentenceRepository.save(sentence);
        return SentenceResponse.from(sentence);
    }

    @Transactional(readOnly = true)
    public SentenceResponse getRandomSentence(Long userId) {
        List<Sentence> randomSentences = sentenceRepository.findRandomSentences(userId);
        if (randomSentences.isEmpty()) {
            throw new IllegalStateException("조회할 수 있는 문장이 없습니다.");
        }
        return SentenceResponse.from(randomSentences.get(0));
    }

    @Transactional(readOnly = true)
    public TopSentencesResponse getTopSentences() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        
        List<Sentence> todaySentences = sentenceRepository.findTop10ByCreatedAtAfterOrderByCreatedAtDesc(now);
        List<Sentence> yesterdaySentences = sentenceRepository.findTop10ByCreatedAtBetweenOrderByCreatedAtDesc(
                yesterday, now);

        return TopSentencesResponse.builder()
                .today(todaySentences.stream().map(SentenceResponse::from).collect(Collectors.toList()))
                .yesterday(yesterdaySentences.stream().map(SentenceResponse::from).collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public void addEmpathy(Long userId, Long sentenceId) {
        Sentence sentence = sentenceRepository.findById(sentenceId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문장입니다."));
        
        if (sentence.getUser().getId().equals(userId)) {
            throw new IllegalStateException("자신의 문장에는 공감할 수 없습니다.");
        }

        sentence.incrementEmpathyCount();
    }

    @Transactional
    public void removeEmpathy(Long userId, Long sentenceId) {
        Sentence sentence = sentenceRepository.findById(sentenceId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문장입니다."));
        
        if (sentence.getUser().getId().equals(userId)) {
            throw new IllegalStateException("자신의 문장에는 공감을 취소할 수 없습니다.");
        }

        if (sentence.getEmpathyCount() > 0) {
            sentence.decrementEmpathyCount();
        }
    }

    @Transactional(readOnly = true)
    public boolean hasWrittenToday(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        return sentenceRepository.existsByUserAndCreatedAtBetween(user, startOfDay, now);
    }
} 