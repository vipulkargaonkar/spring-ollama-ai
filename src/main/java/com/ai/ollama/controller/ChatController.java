package com.ai.ollama.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @GetMapping("/chat")
    public ResponseEntity<String> chat(
            @RequestParam String query
    ) {
        String response = chatClient.prompt(query).call().content();
        return ResponseEntity.ok(response);
    }

}
