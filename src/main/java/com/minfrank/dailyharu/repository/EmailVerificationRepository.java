package com.minfrank.dailyharu.repository;

import com.minfrank.dailyharu.domain.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByEmail(String email);
    Optional<EmailVerification> findByToken(String token);
    
    @Modifying
    @Query("DELETE FROM EmailVerification e WHERE e.expiryDate < :date")
    int deleteAllByExpiryDateBefore(@Param("date") LocalDateTime date);
} 