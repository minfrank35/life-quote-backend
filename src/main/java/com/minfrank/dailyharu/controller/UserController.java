package com.minfrank.dailyharu.controller;

import com.minfrank.dailyharu.dto.UpdateProfileRequest;
import com.minfrank.dailyharu.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@   Tag(name = "사용자 API", description = "사용자 정보 조회 및 수정 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    
    @Operation(summary = "프로필 수정", description = "사용자의 프로필 정보를 수정합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/me")
    public ResponseEntity<Void> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid UpdateProfileRequest request) {
        Long userId = Long.parseLong(userDetails.getUsername());
        userService.updateProfile(userId, request);
        return ResponseEntity.ok().build();
    }
} 