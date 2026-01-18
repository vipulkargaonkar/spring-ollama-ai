package com.ai.ollama.controller;

import com.ai.ollama.model.ResponseModel;
import com.ai.ollama.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/chat")
    public ResponseEntity<List<ResponseModel>> chat(
            @RequestParam String query,
            @RequestHeader String userId
    ) {
        List<ResponseModel> response = chatService.chat(query, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/advisor-chat")
    public ResponseEntity<String> advisorChat(
            @RequestParam String query,
            @RequestHeader String userId
    ) {
        return ResponseEntity.ok(chatService.advisorChatTemplate(query, userId));
    }

    @GetMapping("/stream-chat")
    public ResponseEntity<Flux<String>> streamChat(
            @RequestParam String query,
            @RequestHeader String userId
    ) {
        return ResponseEntity.ok(this.chatService.streamChat(query, userId));
    }

    @GetMapping("/rag-chat")
    public ResponseEntity<String> ragChat(
            @RequestParam String query,
            @RequestHeader String userId
    ) {
        return ResponseEntity.ok(chatService.ragChat(query, userId));
    }

    @GetMapping("/rag-chat-qa-advisor")
    public ResponseEntity<String> ragChatQuestionAnswerAdvisor(
            @RequestParam String query,
            @RequestHeader String userId
    ) {
        return ResponseEntity.ok(chatService.ragChatQuestionAnswerAdvisor(query, userId));
    }

    @GetMapping("/rag-chat-ra-advisor")
    public ResponseEntity<String> ragChatRetrievalAugmentationAdvisor(
            @RequestParam String query,
            @RequestHeader String userId
    ) {
        return ResponseEntity.ok(chatService.ragChatRetrievalAugmentationAdvisor(query, userId));
    }

    @GetMapping("/advanced-rag-chat")
    public ResponseEntity<String> advancedRagChat(
            @RequestParam String query,
            @RequestHeader String userId
    ) {
        return ResponseEntity.ok(chatService.advancedRagChat(query, userId));
    }

    @GetMapping("/tool-calling")
    public ResponseEntity<String> toolCalling(
            @RequestParam String query,
            @RequestHeader String userId
    ) {
        return ResponseEntity.ok(chatService.toolCalling(query, userId));
    }
}
