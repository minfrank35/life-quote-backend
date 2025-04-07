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
    
    @Column(nullable = false)
    private String content;
    
    @Column(nullable = false)
    private LocalDate writtenDate;
    
    private int empathyCount;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @Builder
    public Sentence(User user, String content, LocalDate writtenDate) {
        this.user = user;
        this.content = content;
        this.writtenDate = writtenDate;
        this.empathyCount = 0;
    }
    
    public void increaseEmpathyCount() {
        this.empathyCount++;
    }
} 