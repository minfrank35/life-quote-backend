package com.minfrank.dailyharu.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class MainResponse {
    private final SentenceResponse todayTopSentence;
    private final SentenceResponse yesterdayTopSentence;
    private final List<SentenceResponse> recentSentences;
    private final boolean hasWrittenToday;

    public MainResponse(SentenceResponse todayTopSentence,
                       SentenceResponse yesterdayTopSentence,
                       List<SentenceResponse> recentSentences,
                       boolean hasWrittenToday) {
        this.todayTopSentence = todayTopSentence;
        this.yesterdayTopSentence = yesterdayTopSentence;
        this.recentSentences = recentSentences;
        this.hasWrittenToday = hasWrittenToday;
    }
} 