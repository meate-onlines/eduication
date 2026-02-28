package com.eduication.language.repository;

import com.eduication.language.entity.MarketingArticle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarketingArticleRepository extends JpaRepository<MarketingArticle, Long> {
    List<MarketingArticle> findByPublishedTrueOrderByCreatedAtDesc();
}
