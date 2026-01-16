package com.ai.ollama.service.impl;

import com.ai.ollama.ResponseModel;
import com.ai.ollama.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;

@Service
public class ChatServiceImpl implements ChatService {

    @Value("classpath:/prompts/user-message.st")
    private Resource userMessage;

    private final ChatClient chatClient;

    public ChatServiceImpl(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public List<ResponseModel> chat(String query) {
        Prompt prompt = new Prompt(query);
        return Objects.requireNonNull(chatClient
                .prompt(prompt)
                .call()
                .entity(new ParameterizedTypeReference<List<ResponseModel>>() {
                }));
    }

    @Override
    public String advisorChatTemplate(String query) {
        return this.chatClient
                .prompt()
                .user(user -> user.text(this.userMessage).param("concept", query))
                .call()
                .content();
    }

    @Override
    public Flux<String> streamChat(String query) {
        return this.chatClient
                .prompt()
                .user(user -> user.text(this.userMessage).param("concept", query))
                .stream()
                .content();
    }
}
