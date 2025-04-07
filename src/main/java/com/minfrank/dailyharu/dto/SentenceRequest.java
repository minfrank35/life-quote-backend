package com.minfrank.dailyharu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SentenceRequest {
    @NotBlank(message = "문장 내용은 필수입니다.")
    @Size(max = 200, message = "문장은 200자를 초과할 수 없습니다.")
    private String content;
} 