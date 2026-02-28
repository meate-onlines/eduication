package com.eduication.language.controller;

import com.eduication.language.dto.ApiResponse;
import com.eduication.language.enums.ModelProvider;
import com.eduication.language.service.AiTeachingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiTeachingService aiTeachingService;

    public AiController(AiTeachingService aiTeachingService) {
        this.aiTeachingService = aiTeachingService;
    }

    @PostMapping("/resources/generate")
    public ApiResponse<Map<String, String>> generateResource(@Valid @RequestBody GenerateResourceRequest request) {
        String content = aiTeachingService.generateTeachingResource(
                request.provider(), request.language(), request.level(), request.topic());
        return ApiResponse.ok("生成成功", Map.of("content", content));
    }

    @PostMapping("/chat")
    public ApiResponse<Map<String, String>> chat(@Valid @RequestBody TutorChatRequest request) {
        String reply = aiTeachingService.chatWithTutor(
                request.provider(), request.language(), request.userInput());
        return ApiResponse.ok("回答成功", Map.of("reply", reply));
    }

    public record GenerateResourceRequest(
            @NotNull(message = "模型渠道不能为空")
            ModelProvider provider,
            @NotBlank(message = "语种不能为空")
            String language,
            @NotBlank(message = "学习级别不能为空")
            String level,
            @NotBlank(message = "学习主题不能为空")
            String topic
    ) {
    }

    public record TutorChatRequest(
            @NotNull(message = "模型渠道不能为空")
            ModelProvider provider,
            @NotBlank(message = "语种不能为空")
            String language,
            @NotBlank(message = "对话内容不能为空")
            String userInput
    ) {
    }
}
