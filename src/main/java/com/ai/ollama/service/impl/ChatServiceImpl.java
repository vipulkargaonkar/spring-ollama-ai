package com.ai.ollama.service.impl;

import com.ai.ollama.model.ResponseModel;
import com.ai.ollama.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.join.ConcatenationDocumentJoiner;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Value("classpath:/prompts/rag-system-message.st")
    private Resource ragSystemMessage;

    @Value("classpath:/prompts/rag-user-message.st")
    private Resource ragUserMessage;

    private final ChatClient chatClient;
    private final ChatClient ragChatClient;
    private final VectorStore vectorStore;

    public ChatServiceImpl(ChatClient chatClient, @Qualifier("ragChatClient") ChatClient ragChatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.ragChatClient = ragChatClient;
        this.vectorStore = vectorStore;
    }

    @Override
    public List<ResponseModel> chat(String query, String userId) {
        Prompt prompt = new Prompt(query);
        return Objects.requireNonNull(chatClient
                .prompt(prompt)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, userId))
                .call()
                .entity(new ParameterizedTypeReference<List<ResponseModel>>() {
                }));
    }

    @Override
    public String advisorChatTemplate(String query, String userId) {
        return this.chatClient
                .prompt()
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, userId))
                .user(user -> user.text(this.userMessage).param("concept", query))
                .call()
                .content();
    }

    @Override
    public Flux<String> streamChat(String query, String userId) {
        return this.chatClient
                .prompt()
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, userId))
                .user(user -> user.text(this.userMessage).param("concept", query))
                .stream()
                .content();
    }

    @Override
    public String ragChat(String query, String userId) {
        SearchRequest searchRequest = SearchRequest.builder()
                .topK(5)
                .similarityThreshold(0.5)
                .query(query)
                .build();

        List<Document> documents = vectorStore.similaritySearch(searchRequest);

        if (documents.isEmpty()) {
            return "This query is not in the database.";
        }

        List<String> documentList = documents.stream().map(Document::getText).toList();
        String contextData = String.join(", ", documentList);

        return this.ragChatClient
                .prompt()
                .system(system -> system.text(this.ragSystemMessage).param("documents", contextData))
                .user(user -> user.text(this.ragUserMessage).param("query", query))
                .call()
                .content();
    }

    @Override
    public String ragChatQuestionAnswerAdvisor(String query, String userId) {
        return this.ragChatClient
                .prompt()
                .advisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                .user(user -> user.text(this.ragUserMessage).param("query", query))
                .call()
                .content();
    }

    @Override
    public String ragChatRetrievalAugmentationAdvisor(String query, String userId) {
        var advisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(this.vectorStore)
                        .topK(3)
                        .similarityThreshold(0.5)
                        .build())
                .queryAugmenter(ContextualQueryAugmenter.builder().allowEmptyContext(true).build())
                .build();

        return this.ragChatClient
                .prompt()
                .advisors(advisor)
                .user(user -> user.text(this.ragUserMessage).param("query", query))
                .call()
                .content();
    }

    @Override
    public String advancedRagChat(String query, String userId) {
        var advisor = RetrievalAugmentationAdvisor.builder()
                .queryTransformers(RewriteQueryTransformer.builder()
                        .chatClientBuilder(this.ragChatClient.mutate().clone())
                        .build())
                .queryExpander(MultiQueryExpander.builder()
                        .chatClientBuilder(this.ragChatClient.mutate().clone())
                        .numberOfQueries(3)
                        .build())
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(this.vectorStore)
                        .topK(3)
                        .similarityThreshold(0.3)
                        .build())
                .documentJoiner(new ConcatenationDocumentJoiner())
                .queryAugmenter(ContextualQueryAugmenter.builder().build())
                .build();

        return this.ragChatClient
                .prompt()
                .advisors(advisor)
                .user(query)
                .call()
                .content();
    }

    @Override
    public void saveData(List<String> list) {
        List<Document> documentList = list.stream().map(Document::new).toList();
        this.vectorStore.add(documentList);
    }

}
