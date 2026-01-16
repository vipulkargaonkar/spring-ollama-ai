package com.ai.ollama.service;

import com.ai.ollama.ResponseModel;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {

    List<ResponseModel> chat(String query);

    String advisorChatTemplate(String query);

    Flux<String> streamChat(String query);
}
