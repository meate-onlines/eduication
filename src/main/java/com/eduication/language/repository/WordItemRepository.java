package com.eduication.language.repository;

import com.eduication.language.entity.WordItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordItemRepository extends JpaRepository<WordItem, Long> {
    List<WordItem> findByLanguageOrderByCreatedAtDesc(String language);
}
