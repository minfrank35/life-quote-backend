package com.minfrank.dailyharu.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TopSentencesResponse {
    private final List<SentenceResponse> today;
    private final List<SentenceResponse> yesterday;
} 