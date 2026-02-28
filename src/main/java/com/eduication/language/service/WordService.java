package com.eduication.language.service;

import com.eduication.language.entity.UserAccount;
import com.eduication.language.entity.UserWordProgress;
import com.eduication.language.entity.WordItem;
import com.eduication.language.enums.AccessLevel;
import com.eduication.language.exception.BusinessException;
import com.eduication.language.repository.UserWordProgressRepository;
import com.eduication.language.repository.WordItemRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WordService {

    private final WordItemRepository wordItemRepository;
    private final UserWordProgressRepository userWordProgressRepository;

    public WordService(WordItemRepository wordItemRepository,
                       UserWordProgressRepository userWordProgressRepository) {
        this.wordItemRepository = wordItemRepository;
        this.userWordProgressRepository = userWordProgressRepository;
    }

    public WordItem createWord(String language,
                               String word,
                               String phonetic,
                               String meaning,
                               String exampleSentence,
                               AccessLevel accessLevel) {
        WordItem item = new WordItem();
        item.setLanguage(language);
        item.setWord(word);
        item.setPhonetic(phonetic);
        item.setMeaning(meaning);
        item.setExampleSentence(exampleSentence);
        item.setAccessLevel(accessLevel);
        return wordItemRepository.save(item);
    }

    public List<WordItem> listWordsForUser(String language, UserAccount user) {
        return wordItemRepository.findByLanguageOrderByCreatedAtDesc(language)
                .stream()
                .filter(item -> item.getAccessLevel() == AccessLevel.NORMAL || user.isVip())
                .toList();
    }

    public UserWordProgress reviewWord(Long wordId, Integer familiarity, UserAccount user) {
        if (familiarity < 1 || familiarity > 5) {
            throw new BusinessException("熟悉度范围应为1-5");
        }
        WordItem item = wordItemRepository.findById(wordId)
                .orElseThrow(() -> new BusinessException("单词不存在"));
        if (item.getAccessLevel() == AccessLevel.VIP && !user.isVip()) {
            throw new BusinessException("该单词为会员资源，请先订阅");
        }
        UserWordProgress progress = userWordProgressRepository
                .findByUserIdAndWordItemId(user.getId(), wordId)
                .orElseGet(() -> {
                    UserWordProgress created = new UserWordProgress();
                    created.setUser(user);
                    created.setWordItem(item);
                    created.setReviewCount(0);
                    created.setFamiliarity(1);
                    return created;
                });
        int reviewCount = progress.getReviewCount() == null ? 0 : progress.getReviewCount();
        progress.setReviewCount(reviewCount + 1);
        progress.setFamiliarity(familiarity);
        LocalDateTime now = LocalDateTime.now();
        progress.setLastReviewedAt(now);
        progress.setNextReviewAt(now.plusDays(Math.max(1, familiarity - 1L)));
        return userWordProgressRepository.save(progress);
    }

    public List<UserWordProgress> listProgress(UserAccount user) {
        return userWordProgressRepository.findByUserIdOrderByUpdatedAtDesc(user.getId());
    }
}
