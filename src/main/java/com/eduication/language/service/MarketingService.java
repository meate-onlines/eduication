package com.eduication.language.service;

import com.eduication.language.entity.MarketingArticle;
import com.eduication.language.exception.BusinessException;
import com.eduication.language.repository.MarketingArticleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarketingService {

    private final MarketingArticleRepository marketingArticleRepository;

    public MarketingService(MarketingArticleRepository marketingArticleRepository) {
        this.marketingArticleRepository = marketingArticleRepository;
    }

    public List<MarketingArticle> listPublished() {
        return marketingArticleRepository.findByPublishedTrueOrderByCreatedAtDesc();
    }

    public MarketingArticle getById(Long id) {
        return marketingArticleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("软文不存在"));
    }

    public MarketingArticle create(String title, String summary, String content, boolean published) {
        MarketingArticle article = new MarketingArticle();
        article.setTitle(title);
        article.setSummary(summary);
        article.setContent(content);
        article.setPublished(published);
        return marketingArticleRepository.save(article);
    }
}
