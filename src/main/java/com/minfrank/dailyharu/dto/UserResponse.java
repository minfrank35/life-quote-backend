package com.minfrank.dailyharu.dto;

import com.minfrank.dailyharu.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private final Long id;
    private final String email;
    private final String nickname;
    private final boolean emailVerified;

    public static UserResponse from(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .nickname(user.getNickname())
            .emailVerified(user.isEmailVerified())
            .build();
    }
} 