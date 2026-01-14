package com.ai.ollama.service;

import com.ai.ollama.ResponseModel;

import java.util.List;

public interface ChatService {

    List<ResponseModel> chat(String query);
}
