package com.ls.mao.joule.service.impl;

import com.ls.mao.joule.factory.ChatClientFactory;
import com.ls.mao.joule.model.Assistant;
import com.ls.mao.joule.repo.AssistantRepository;
import com.ls.mao.joule.service.AssistantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
public class AssistantServiceImpl implements AssistantService {

    private static final Logger logger = LoggerFactory.getLogger(AssistantServiceImpl.class);

    private static final String ASSISTANT_ALREADY_EXISTS = "Assistant with name %s already exists.";
    private static final String ASSISTANT_NOT_FOUND = "No assistant found with name: %s";
    private static final String DEFAULT_NO_ANSWER_MESSAGE = "I'm sorry, I don't have an answer for that.";

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
    @Transactional
    public String registerAssistant(String name, String response, String systemPrompt) {
        logger.info("Registering assistant: {}", name);
        if (assistantRepository.findByName(name) != null) {
            throw new IllegalArgumentException(String.format(ASSISTANT_ALREADY_EXISTS, name));
        }
        systemPrompt = Objects.requireNonNullElse(systemPrompt, defaultSystemPrompt);
        Assistant assistant = new Assistant(name, response, systemPrompt);
        assistantRepository.save(assistant);
        logger.info("Assistant {} registered successfully.", name);
        return "Assistant " + name + " registered successfully.";
    }

    @Override
    public String getResponse(String name) {
        logger.info("Retrieving default response for assistant: {}", name);
        Assistant assistant = getAssistant(name);
        return Optional.ofNullable(assistant.getDefaultResponse())
                .orElseThrow(() -> new NoSuchElementException(String.format(ASSISTANT_NOT_FOUND, name)));
    }

    @Override
    public String getAnswer(String name, String question) {
        logger.info("Retrieving answer for assistant: {}, question: {}", name, question);
        Assistant assistant = getAssistant(name);

        ChatClient chatClient = chatClientFactory.createChatClient(assistant.getSystemPrompt());

        try {
            String filterExpression = String.format("assistant == '%s'", assistant.getName());
            String generatedAnswer = chatClient.prompt(question)
                    .advisors(a -> a.param(QuestionAnswerAdvisor.FILTER_EXPRESSION, filterExpression))
                    .call()
                    .content();

            return Optional.ofNullable(generatedAnswer)
                    .orElse(DEFAULT_NO_ANSWER_MESSAGE);
        } catch (Exception e) {
            logger.error("An error occurred while retrieving the answer for assistant: {}", name, e);
            throw new RuntimeException("An error occurred while retrieving the answer.", e);
        }
    }

    @Override
    public Assistant getAssistant(String name) {
        logger.info("Fetching assistant with name: {}", name);
        return Optional.ofNullable(assistantRepository.findByName(name))
                .orElseThrow(() -> new NoSuchElementException(String.format(ASSISTANT_NOT_FOUND, name)));
    }
}
