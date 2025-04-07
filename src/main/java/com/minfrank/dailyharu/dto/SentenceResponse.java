package com.minfrank.dailyharu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SentenceResponse {
    private String content;
    private String writtenDate;
    private String correction;
    private String correctedAt;
    private String nickname;
    
    public static SentenceResponse success(String content, String writtenDate) {
        return new SentenceResponse(content, writtenDate, null, null, null);
    }
    
    public static SentenceResponse updateSuccess(String content, String writtenDate) {
        return new SentenceResponse(content, writtenDate, null, null, null);
    }
    
    public static SentenceResponse correctionSuccess(String content, String writtenDate, String correction, String correctedAt) {
        return new SentenceResponse(content, writtenDate, correction, correctedAt, null);
    }
} 