package com.minfrank.dailyharu.repository;

import com.minfrank.dailyharu.domain.PushToken;
import com.minfrank.dailyharu.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PushTokenRepository extends JpaRepository<PushToken, Long> {
    Optional<PushToken> findByUserAndToken(User user, String token);
    List<PushToken> findByUserAndIsActive(User user, boolean isActive);
    List<PushToken> findByIsActive(boolean isActive);
} 