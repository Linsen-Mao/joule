package com.ls.mao.joule.service.impl;

import com.ls.mao.joule.model.Assistant;
import com.ls.mao.joule.repo.AssistantRepository;
import com.ls.mao.joule.service.AssistantService;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class AssistantServiceImpl implements AssistantService {

    private final AssistantRepository assistantRepository;
    private final ChatModel chatModel;

    public AssistantServiceImpl(AssistantRepository assistantRepository, ChatModel chatModel) {
        this.assistantRepository = assistantRepository;
        this.chatModel = chatModel;
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
        // Check if the assistant exists
        Assistant assistant = assistantRepository.findByName(name);
        if (assistant == null) {
            return "Assistant not found.";
        }

        String aiGeneratedAnswer = chatModel.call(question);
        return aiGeneratedAnswer != null ? aiGeneratedAnswer : "I'm sorry, I don't have an answer for that.";
    }
}
