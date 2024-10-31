package com.ls.mao.joule.service.impl;

import com.ls.mao.joule.model.Assistant;
import com.ls.mao.joule.repo.AssistantRepository;
import com.ls.mao.joule.service.AssistantService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

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
        Assistant assistant = new Assistant(name, response);
        assistantRepository.save(assistant);
        return "Assistant " + name + " registered successfully.";
    }

    @Override
    public String getResponse(String name) {
        Assistant assistant = assistantRepository.findByName(name);
        return assistant != null ? assistant.getDefaultResponse() : null;
    }

    @Override
    public String getAnswer(String name, String question) {
        Assistant assistant = assistantRepository.findByName(name);
        if (assistant == null) {
            return "Assistant " + name + " not found.";
        }

        try {
            String aiGeneratedAnswer = chatClient.prompt(question).call().content();
            return aiGeneratedAnswer != null ? aiGeneratedAnswer : "I'm sorry, I don't have an answer for that.";
        } catch (Exception e) {
            return "An error occurred while retrieving the answer.";
        }
    }
}
