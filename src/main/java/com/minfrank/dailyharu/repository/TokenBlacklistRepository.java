package com.minfrank.dailyharu.repository;

import com.minfrank.dailyharu.domain.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    boolean existsByToken(String token);
    
    Optional<TokenBlacklist> findByToken(String token);
    
    @Modifying
    @Query("DELETE FROM TokenBlacklist t WHERE t.expiryDate < :now")
    void deleteAllExpiredTokens(@Param("now") LocalDateTime now);
} 