package com.eduication.language.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DomesticAiProperties.class)
public class AiConfig {

    @Bean("domesticOpenAiApi")
    public OpenAiApi domesticOpenAiApi(DomesticAiProperties properties) {
        return OpenAiApi.builder()
                .baseUrl(properties.getBaseUrl())
                .apiKey(properties.getApiKey())
                .build();
    }

    @Bean("domesticChatModel")
    public ChatModel domesticChatModel(@Qualifier("domesticOpenAiApi") OpenAiApi domesticOpenAiApi,
                                       DomesticAiProperties properties) {
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(properties.getModel())
                .temperature(properties.getTemperature())
                .build();
        return OpenAiChatModel.builder()
                .openAiApi(domesticOpenAiApi)
                .defaultOptions(options)
                .build();
    }

    @Bean("foreignChatClient")
    public ChatClient foreignChatClient(@Qualifier("openAiChatModel") ChatModel foreignChatModel) {
        return ChatClient.builder(foreignChatModel).build();
    }

    @Bean("domesticChatClient")
    public ChatClient domesticChatClient(@Qualifier("domesticChatModel") ChatModel domesticChatModel) {
        return ChatClient.builder(domesticChatModel).build();
    }
}
