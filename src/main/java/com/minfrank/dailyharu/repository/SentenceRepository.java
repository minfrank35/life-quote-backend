package com.minfrank.dailyharu.repository;

import com.minfrank.dailyharu.domain.Sentence;
import com.minfrank.dailyharu.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Long> {
    boolean existsByUserAndWrittenDate(User user, LocalDate date);
    
    @Query(value = "SELECT s FROM Sentence s WHERE s.user.id != :userId " +
        "AND s.writtenDate = :date ORDER BY RAND() LIMIT 1")
    Optional<Sentence> findRandomSentenceExceptUser(@Param("userId") Long userId, @Param("date") LocalDate date);
    
    Optional<Sentence> findTopByWrittenDateOrderByEmpathyCountDesc(LocalDate date);
    
    List<Sentence> findTop10ByOrderByCreatedAtDesc();

    @Query("SELECT s FROM Sentence s WHERE s.user.id != :userId ORDER BY RAND()")
    List<Sentence> findRandomSentences(@Param("userId") Long userId);

    List<Sentence> findTop10ByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime dateTime);

    List<Sentence> findTop10ByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);

    boolean existsByUserAndCreatedAtBetween(User user, LocalDateTime start, LocalDateTime end);
} 