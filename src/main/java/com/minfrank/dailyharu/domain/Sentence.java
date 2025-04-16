package com.minfrank.dailyharu.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sentences")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Sentence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(nullable = false, length = 500)
    private String content;
    
    @Column(nullable = false)
    private LocalDate writtenDate = LocalDate.now();
    
    @Column(nullable = false)
    private int empathyCount = 0;
    
    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column
    private String correction;
    
    @Column
    private LocalDateTime correctedAt;
    
    @Column
    private String nickname;
    
    @Builder
    public Sentence(User user, String content, LocalDate writtenDate) {
        this.user = user;
        this.content = content;
        this.writtenDate = writtenDate;
        this.nickname = user.getNickname();
    }
    
    public void incrementEmpathyCount() {
        this.empathyCount++;
    }

    public void decrementEmpathyCount() {
        if (this.empathyCount > 0) {
            this.empathyCount--;
        }
    }
} 