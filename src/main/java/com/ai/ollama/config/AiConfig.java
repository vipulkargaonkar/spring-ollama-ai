package com.ai.ollama.config;

import com.ai.ollama.advisor.TokenPrintAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.List;

@Configuration
public class AiConfig {

    @Value("classpath:/prompts/system-message.st")
    private Resource systemMessage;

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultAdvisors(new TokenPrintAdvisor(), new SafeGuardAdvisor(List.of("illegal", "hacking", "crime")))
                .defaultSystem(systemMessage)
                .defaultOptions(OllamaChatOptions.builder()
                        .model("llama3.1:latest")
                        .temperature(0.5)
                        .build())
                .build();
    }

}
