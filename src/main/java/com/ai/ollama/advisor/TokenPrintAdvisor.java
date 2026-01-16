package com.ai.ollama.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import reactor.core.publisher.Flux;

import java.util.Objects;

public class TokenPrintAdvisor implements CallAdvisor, StreamAdvisor {

    private final Logger logger = LoggerFactory.getLogger(TokenPrintAdvisor.class);

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {

        this.logger.info("Token Print Advisor is called.");
        this.logger.info("Request: {}", chatClientRequest.prompt().getContents());

        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);

        this.logger.info("Token advisor: Response received from the model:");

        this.logger.info("Response: {}", Objects.requireNonNull(chatClientResponse.chatResponse()).getResult().getOutput().getText());

        this.logger.info("Prompt Token : {}", chatClientResponse.chatResponse().getMetadata().getUsage().getPromptTokens());
        this.logger.info("Completion Token : {}", chatClientResponse.chatResponse().getMetadata().getUsage().getCompletionTokens());
        this.logger.info("Total Token consumed: {}", chatClientResponse.chatResponse().getMetadata().getUsage().getTotalTokens());

        return chatClientResponse;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        return streamAdvisorChain.nextStream(chatClientRequest);
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
