package com.ls.mao.joule.service.impl;

import com.ls.mao.joule.factory.ChatClientFactory;
import com.ls.mao.joule.model.Assistant;
import com.ls.mao.joule.repo.AssistantRepository;
import com.ls.mao.joule.service.AssistantService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AssistantServiceImpl implements AssistantService {


    private final AssistantRepository assistantRepository;
    private final ChatClientFactory chatClientFactory;
    private final String defaultSystemPrompt;

    public AssistantServiceImpl(AssistantRepository assistantRepository,
                                ChatClientFactory chatClientFactory,
                                @Value("${chat.default.system.prompt}") String defaultSystemPrompt) {
        this.assistantRepository = assistantRepository;
        this.chatClientFactory = chatClientFactory;
        this.defaultSystemPrompt = defaultSystemPrompt;
    }

    @Override
    public String registerAssistant(String name, String response, String systemPrompt) {
        if (assistantRepository.findByName(name) != null) {
            throw new IllegalArgumentException("Assistant with name " + name + " already exists.");
        }
        if (systemPrompt == null || systemPrompt.isEmpty()) {
            systemPrompt = defaultSystemPrompt;
        }
        Assistant assistant = new Assistant(name, response, systemPrompt);
        assistantRepository.save(assistant);
        return "Assistant " + name + " registered successfully.";
    }

    @Override
    public String getResponse(String name) {
        Assistant assistant = getAssistant(name);
        return Optional.ofNullable(assistant.getDefaultResponse())
                .orElseThrow(() -> new NoSuchElementException("No response found for assistant: " + name));
    }

    @Override
    public String getAnswer(String name, String question) {
        Assistant assistant = getAssistant(name);

        ChatClient chatClient = chatClientFactory.createChatClient(assistant.getSystemPrompt());

        try {
            String filterExpression = String.format("assistant == '%s'", assistant.getName());
            String generatedAnswer = chatClient.prompt(question)
                    .advisors(a -> a.param(QuestionAnswerAdvisor.FILTER_EXPRESSION, filterExpression))
                    .call()
                    .content();

            return Optional.ofNullable(generatedAnswer)
                    .orElse("I'm sorry, I don't have an answer for that.");
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while retrieving the answer.", e);
        }
    }

    @Override
    public Assistant getAssistant(String name) {
        return Optional.ofNullable(assistantRepository.findByName(name))
                .orElseThrow(() -> new NoSuchElementException("No assistant found with name: " + name));
    }
}
