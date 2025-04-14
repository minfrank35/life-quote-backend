package com.minfrank.dailyharu.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Builder
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false, length = 100)  // SHA-256 해시는 64자, 여유있게 100자로 설정
    private String password;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String nickname;
    
    @Column(nullable = false)
    private boolean emailVerified = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider = AuthProvider.LOCAL;
    
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    
    private String providerId;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
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