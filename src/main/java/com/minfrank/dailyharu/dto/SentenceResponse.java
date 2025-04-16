package com.minfrank.dailyharu.dto;

import com.minfrank.dailyharu.domain.Sentence;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class SentenceResponse {
    private final Long id;
    private final String content;
    private final int empathyCount;
    private final LocalDateTime createdAt;
    private final LocalDate writtenDate;
    private final String correction;
    private final String correctedAt;
    private final String nickname;

    public SentenceResponse(Long id, String content, int empathyCount, LocalDateTime createdAt, 
                          LocalDate writtenDate, String correction, String correctedAt, String nickname) {
        this.id = id;
        this.content = content;
        this.empathyCount = empathyCount;
        this.createdAt = createdAt;
        this.writtenDate = writtenDate;
        this.correction = correction;
        this.correctedAt = correctedAt;
        this.nickname = nickname;
    }

    public static SentenceResponse from(Sentence sentence) {
        return new SentenceResponse(
            sentence.getId(),
            sentence.getContent(),
            sentence.getEmpathyCount(),
            sentence.getCreatedAt(),
            sentence.getWrittenDate(),
            sentence.getCorrection(),
            sentence.getCorrectedAt() != null ? sentence.getCorrectedAt().toString() : null,
            sentence.getNickname()
        );
    }
    
    public static SentenceResponse success(String content, String writtenDate) {
        return new SentenceResponse(null, content, 0, null, null, null, null, null);
    }
    
    public static SentenceResponse updateSuccess(String content, String writtenDate) {
        return new SentenceResponse(null, content, 0, null, null, null, null, null);
    }
    
    public static SentenceResponse correctionSuccess(String content, String writtenDate, String correction, String correctedAt) {
        return new SentenceResponse(null, content, 0, null, null, correction, correctedAt, null);
    }
} 