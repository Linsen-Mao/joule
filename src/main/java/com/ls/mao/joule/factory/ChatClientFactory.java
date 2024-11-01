package com.ls.mao.joule.factory;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ChatClientFactory {

    @Value("${spring.ai.vectorstore.pgvector.activated}")
    private boolean isPgVectorActivated;

    private final VectorStore vectorStore;
    private ChatClient.Builder builder;

    public ChatClientFactory(Optional<VectorStore> vectorStore, ChatClient.Builder builder) {
        this.vectorStore = vectorStore.orElse(null);
        this.builder = builder;
    }

    public ChatClient createChatClient(String systemPrompt) {
        builder.defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()));

        if (systemPrompt != null) {
            builder.defaultSystem(systemPrompt);
        }

        if (isPgVectorActivated && vectorStore != null) {
            builder.defaultAdvisors(new QuestionAnswerAdvisor(vectorStore));
        }

        return builder.build();
    }
}
