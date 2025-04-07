package com.minfrank.dailyharu.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String email;
    
    private String password;
    
    private String nickname;
    
    private boolean emailVerified;
    
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;
    
    private String providerId;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @Builder
    public User(String email, String password, String nickname, 
                AuthProvider provider, String providerId) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.provider = provider != null ? provider : AuthProvider.LOCAL;
        this.providerId = providerId;
        this.emailVerified = provider != AuthProvider.LOCAL;
    }
    
    public void verifyEmail() {
        this.emailVerified = true;
    }
    
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
    
    public void updateProfile(String nickname) {
        this.nickname = nickname;
    }
} 