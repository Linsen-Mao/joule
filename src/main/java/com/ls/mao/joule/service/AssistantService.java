package com.ls.mao.joule.service;

import com.ls.mao.joule.model.Assistant;
import com.ls.mao.joule.repo.AssistantRepository;
import org.springframework.stereotype.Service;

@Service
public class AssistantService {

    private final AssistantRepository assistantRepository;

    public AssistantService(AssistantRepository assistantRepository) {
        this.assistantRepository = assistantRepository;
    }


    public String registerAssistant(String name, String response) {
        Assistant assistant = new Assistant(name, response);
        assistantRepository.save(assistant);
        return "Assistant " + name + " registered successfully.";
    }

    public String getResponse(String name) {
        Assistant assistant = assistantRepository.findByName(name);
        return assistant != null ? assistant.getDefaultResponse() : null;
    }

    //TODO
    public String addQuestionAnswer(String name, String question, String answer) {
        Assistant assistant = assistantRepository.findByName(name);
        if (assistant != null) {
            assistant.addQuestionAnswer(question, answer);
            return "Question-Answer pair added successfully.";
        }
        return "Assistant not found.";
    }

    //TODO
    public String getAnswer(String name, String question) {
        Assistant assistant = assistantRepository.findByName(name);
        return assistant != null ? assistant.getAnswer(question) : null;
    }
}
