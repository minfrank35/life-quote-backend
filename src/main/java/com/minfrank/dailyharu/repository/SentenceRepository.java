package com.minfrank.dailyharu.repository;

import com.minfrank.dailyharu.domain.Sentence;
import com.minfrank.dailyharu.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Long> {
    boolean existsByUserAndWrittenDate(User user, LocalDate writtenDate);
    
    @Query(value = "SELECT s FROM Sentence s WHERE s.user.id != :userId " +
        "AND s.writtenDate = :date ORDER BY RAND() LIMIT 1")
    Optional<Sentence> findRandomSentenceExceptUser(@Param("userId") Long userId, @Param("date") LocalDate date);
} 