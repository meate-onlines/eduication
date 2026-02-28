package com.eduication.language.repository;

import com.eduication.language.entity.UserWordProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserWordProgressRepository extends JpaRepository<UserWordProgress, Long> {
    List<UserWordProgress> findByUserIdOrderByUpdatedAtDesc(Long userId);

    Optional<UserWordProgress> findByUserIdAndWordItemId(Long userId, Long wordItemId);
}
