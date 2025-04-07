package com.minfrank.dailyharu.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class RefreshToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String tokenKey;
    
    @Column(nullable = false, length = 1000)
    private String tokenValue;
    
    @Column(nullable = false)
    private LocalDateTime expiryDate;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @Builder
    public RefreshToken(String tokenKey, String tokenValue, LocalDateTime expiryDate) {
        this.tokenKey = tokenKey;
        this.tokenValue = tokenValue;
        this.expiryDate = expiryDate;
    }
    
    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }
    
    public void updateToken(String tokenValue, LocalDateTime expiryDate) {
        this.tokenValue = tokenValue;
        this.expiryDate = expiryDate;
    }
} 