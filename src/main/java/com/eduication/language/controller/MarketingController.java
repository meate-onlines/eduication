package com.eduication.language.controller;

import com.eduication.language.dto.ApiResponse;
import com.eduication.language.entity.MarketingArticle;
import com.eduication.language.service.MarketingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/marketing")
public class MarketingController {

    private final MarketingService marketingService;

    public MarketingController(MarketingService marketingService) {
        this.marketingService = marketingService;
    }

    @GetMapping("/public/articles")
    public ApiResponse<List<MarketingArticle>> listPublished() {
        return ApiResponse.ok("查询成功", marketingService.listPublished());
    }

    @GetMapping("/public/articles/{id}")
    public ApiResponse<MarketingArticle> article(@PathVariable Long id) {
        return ApiResponse.ok("查询成功", marketingService.getById(id));
    }

    @PostMapping("/articles")
    public ApiResponse<MarketingArticle> create(@Valid @RequestBody CreateArticleRequest request) {
        MarketingArticle saved = marketingService.create(
                request.title(), request.summary(), request.content(), request.published());
        return ApiResponse.ok("创建成功", saved);
    }

    public record CreateArticleRequest(
            @NotBlank(message = "标题不能为空")
            @Size(max = 150, message = "标题不能超过150字")
            String title,
            @Size(max = 300, message = "摘要不能超过300字")
            String summary,
            @NotBlank(message = "内容不能为空")
            String content,
            boolean published
    ) {
    }
}
