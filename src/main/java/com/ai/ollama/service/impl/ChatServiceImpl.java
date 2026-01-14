package com.ai.ollama.service.impl;

import com.ai.ollama.ResponseModel;
import com.ai.ollama.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;

    public ChatServiceImpl(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
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
}
