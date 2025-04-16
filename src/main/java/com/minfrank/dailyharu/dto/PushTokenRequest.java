package com.minfrank.dailyharu.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PushTokenRequest {
    @NotBlank(message = "푸시 토큰은 필수입니다.")
    private String token;

    @NotBlank(message = "디바이스 타입은 필수입니다.")
    private String deviceType; // iOS, Android
} 