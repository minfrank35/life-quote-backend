package com.minfrank.dailyharu.controller;

import com.minfrank.dailyharu.dto.UpdateProfileRequest;
import com.minfrank.dailyharu.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    
    @PutMapping("/me")
    public ResponseEntity<Void> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid UpdateProfileRequest request) {
        Long userId = Long.parseLong(userDetails.getUsername());
        userService.updateProfile(userId, request);
        return ResponseEntity.ok().build();
    }
} 