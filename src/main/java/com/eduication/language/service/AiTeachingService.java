package com.eduication.language.service;

import com.eduication.language.enums.ModelProvider;
import com.eduication.language.exception.BusinessException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AiTeachingService {

    private final ChatClient foreignChatClient;
    private final ChatClient domesticChatClient;

    public AiTeachingService(@Qualifier("foreignChatClient") ChatClient foreignChatClient,
                             @Qualifier("domesticChatClient") ChatClient domesticChatClient) {
        this.foreignChatClient = foreignChatClient;
        this.domesticChatClient = domesticChatClient;
    }

    public String generateTeachingResource(ModelProvider provider, String language, String level, String topic) {
        String prompt = """
                你是一名资深外语教师，请围绕以下参数生成教学内容：
                语种：%s
                级别：%s
                主题：%s

                输出要求：
                1. 一段课程导语
                2. 5个核心知识点
                3. 10个重点词汇（含释义）
                4. 3道练习题（含答案）
                请使用中文组织解释，并给出学习建议。
                """.formatted(language, level, topic);
        return chooseClient(provider).prompt()
                .system("你是在线外语学习平台的教学内容生成助手。")
                .user(prompt)
                .call()
                .content();
    }

    public String chatWithTutor(ModelProvider provider, String language, String userInput) {
        return chooseClient(provider).prompt()
                .system("你是耐心的外语口语教练，请先纠错再引导，默认用中文解释并附上目标语种示例。目标语种：" + language)
                .user(userInput)
                .call()
                .content();
    }

    private ChatClient chooseClient(ModelProvider provider) {
        if (provider == null) {
            throw new BusinessException("模型渠道不能为空");
        }
        return switch (provider) {
            case DOMESTIC -> domesticChatClient;
            case FOREIGN -> foreignChatClient;
        };
    }
}
