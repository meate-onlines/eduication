package com.eduication.language.controller;

import com.eduication.language.dto.ApiResponse;
import com.eduication.language.entity.UserAccount;
import com.eduication.language.entity.UserWordProgress;
import com.eduication.language.entity.WordItem;
import com.eduication.language.enums.AccessLevel;
import com.eduication.language.service.CurrentUserService;
import com.eduication.language.service.WordService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/words")
public class WordController {

    private final WordService wordService;
    private final CurrentUserService currentUserService;

    public WordController(WordService wordService,
                          CurrentUserService currentUserService) {
        this.wordService = wordService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    public ApiResponse<WordItem> createWord(@Valid @RequestBody CreateWordRequest request) {
        WordItem saved = wordService.createWord(
                request.language(),
                request.word(),
                request.phonetic(),
                request.meaning(),
                request.exampleSentence(),
                request.accessLevel());
        return ApiResponse.ok("单词创建成功", saved);
    }

    @GetMapping
    public ApiResponse<List<WordItem>> listWords(@RequestParam String language) {
        UserAccount user = currentUserService.requireCurrentUser();
        return ApiResponse.ok("查询成功", wordService.listWordsForUser(language, user));
    }

    @PostMapping("/{wordId}/review")
    public ApiResponse<WordProgressResponse> review(@PathVariable Long wordId,
                                                    @Valid @RequestBody ReviewWordRequest request) {
        UserAccount user = currentUserService.requireCurrentUser();
        UserWordProgress progress = wordService.reviewWord(wordId, request.familiarity(), user);
        return ApiResponse.ok("打卡成功", toProgressResponse(progress));
    }

    @GetMapping("/progress")
    public ApiResponse<List<WordProgressResponse>> progress() {
        UserAccount user = currentUserService.requireCurrentUser();
        List<WordProgressResponse> result = wordService.listProgress(user)
                .stream()
                .map(this::toProgressResponse)
                .toList();
        return ApiResponse.ok("查询成功", result);
    }

    public record CreateWordRequest(
            @NotBlank(message = "语种不能为空")
            String language,
            @NotBlank(message = "单词不能为空")
            String word,
            String phonetic,
            @NotBlank(message = "词义不能为空")
            String meaning,
            String exampleSentence,
            @NotNull(message = "资源权限不能为空")
            AccessLevel accessLevel
    ) {
    }

    public record ReviewWordRequest(
            @NotNull(message = "熟悉度不能为空")
            @Min(value = 1, message = "熟悉度最小为1")
            @Max(value = 5, message = "熟悉度最大为5")
            Integer familiarity
    ) {
    }

    public record WordProgressResponse(
            Long progressId,
            Long wordId,
            String word,
            String meaning,
            Integer familiarity,
            Integer reviewCount,
            LocalDateTime lastReviewedAt,
            LocalDateTime nextReviewAt
    ) {
    }

    private WordProgressResponse toProgressResponse(UserWordProgress progress) {
        return new WordProgressResponse(
                progress.getId(),
                progress.getWordItem().getId(),
                progress.getWordItem().getWord(),
                progress.getWordItem().getMeaning(),
                progress.getFamiliarity(),
                progress.getReviewCount(),
                progress.getLastReviewedAt(),
                progress.getNextReviewAt()
        );
    }
}
