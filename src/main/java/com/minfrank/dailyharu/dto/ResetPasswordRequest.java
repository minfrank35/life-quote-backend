package com.minfrank.dailyharu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResetPasswordRequest {
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(regexp = "^[a-fA-F0-9]{64}$", message = "비밀번호는 SHA-256 해시 형식이어야 합니다.")
    private String newPassword;
} 