package com.minfrank.dailyharu.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PushToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private String deviceType; // iOS, Android

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean isActive;

    public static PushToken create(User user, String token, String deviceType) {
        PushToken pushToken = new PushToken();
        pushToken.user = user;
        pushToken.token = token;
        pushToken.deviceType = deviceType;
        pushToken.createdAt = LocalDateTime.now();
        pushToken.isActive = true;
        return pushToken;
    }

    public void deactivate() {
        this.isActive = false;
    }
} 