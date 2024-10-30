package com.ls.mao.joule.service.impl;

import com.ls.mao.joule.model.ApiResponse;
import com.ls.mao.joule.model.Assistant;
import com.ls.mao.joule.model.QuestionRequest;
import com.ls.mao.joule.repo.AssistantRepository;
import com.ls.mao.joule.service.AssistantService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Implementation of IAssistantService providing assistant-related operations.
 */
@Service
public class AssistantServiceImpl implements AssistantService {

    private final AssistantRepository assistantRepository;

    public AssistantServiceImpl(AssistantRepository assistantRepository) {
        this.assistantRepository = assistantRepository;
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
    public String addQuestionAnswer(String name, String question, String answer) {
        Assistant assistant = assistantRepository.findByName(name);
        if (assistant != null) {
            assistant.addQuestionAnswer(question, answer);
            assistantRepository.save(assistant);  // Save changes to the database
            return "Question-Answer pair added successfully.";
        }
        return "Assistant not found.";
    }

    @Override
    public String getAnswer(String name, String question) {
        Assistant assistant = assistantRepository.findByName(name);
        return assistant != null ? assistant.getAnswer(question) : null;
    }
}
