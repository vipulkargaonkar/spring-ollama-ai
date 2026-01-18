package com.ai.ollama.service;

import com.ai.ollama.model.ResponseModel;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {

    List<ResponseModel> chat(String query, String userId);

    String advisorChatTemplate(String query, String userId);

    Flux<String> streamChat(String query, String userId);

    void saveData(List<String> list);

    String ragChat(String query, String userId);

    String ragChatQuestionAnswerAdvisor(String query, String userId);

    String ragChatRetrievalAugmentationAdvisor(String query, String userId);

    String advancedRagChat(String query, String userId);
}
