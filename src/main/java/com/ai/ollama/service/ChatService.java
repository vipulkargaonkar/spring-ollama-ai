package com.ai.ollama.service;

import com.ai.ollama.model.ResponseModel;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {

    List<ResponseModel> chat(String query, String userId);

    String advisorChatTemplate(String query, String userId);

    Flux<String> streamChat(String query, String userId);

    void saveData(List<String> list);
}
