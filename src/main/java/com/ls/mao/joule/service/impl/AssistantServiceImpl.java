package com.ls.mao.joule.service.impl;

import com.ls.mao.joule.model.Assistant;
import com.ls.mao.joule.repo.AssistantRepository;
import com.ls.mao.joule.service.AssistantService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AssistantServiceImpl implements AssistantService {

    private final AssistantRepository assistantRepository;
    private final ChatClient chatClient;

    public AssistantServiceImpl(AssistantRepository assistantRepository, ChatClient chatClient) {
        this.assistantRepository = assistantRepository;
        this.chatClient = chatClient;
    }

    @Override
    public String registerAssistant(String name, String response) {
        Assistant existingAssistant = assistantRepository.findByName(name);
        if (existingAssistant != null) {
            throw new IllegalArgumentException("Assistant with name " + name + " already exists.");
        }
        Assistant assistant = new Assistant(name, response);
        assistantRepository.save(assistant);
        return "Assistant " + name + " registered successfully.";
    }

    @Override
    public String getResponse(String name) {
        Assistant assistant = Optional.ofNullable(assistantRepository.findByName(name))
                .orElseThrow(() -> new NoSuchElementException("No assistant found with name: " + name));
        return Optional.ofNullable(assistant.getDefaultResponse())
                .orElseThrow(() -> new NoSuchElementException("No response found for assistant: " + name));
    }

    @Override
    public String getAnswer(String name, String question) {
        Assistant assistant = Optional.ofNullable(assistantRepository.findByName(name))
                .orElseThrow(() -> new NoSuchElementException("No assistant found with name: " + name));
        try {
            String generatedAnswer = chatClient.prompt(question).call().content();
            return Optional.ofNullable(generatedAnswer)
                    .orElse("I'm sorry, I don't have an answer for that.");
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while retrieving the answer.", e);
        }
    }
}
