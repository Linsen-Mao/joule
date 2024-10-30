package com.ls.mao.joule.service;

import com.ls.mao.joule.model.ApiResponse;
import com.ls.mao.joule.model.Assistant;
import com.ls.mao.joule.model.QuestionRequest;
import org.springframework.http.ResponseEntity;

/**
 * Interface defining assistant-related operations.
 */
public interface AssistantService {

    /**
     * Registers a new assistant with a default response.
     *
     * @param name     the name of the assistant
     * @param response the default response for the assistant
     * @return a success message
     */
    String registerAssistant(String name, String response);

    /**
     * Retrieves the default response of the assistant with the given name.
     *
     * @param name the name of the assistant
     * @return the assistant's default response, or null if not found
     */
    String getResponse(String name);

    /**
     * Adds a question-answer pair to the specified assistant.
     *
     * @param name     the name of the assistant
     * @param question the question to add
     * @param answer   the answer to the question
     * @return a success message if added successfully, or an error if assistant not found
     */
    String addQuestionAnswer(String name, String question, String answer);

    /**
     * Retrieves the answer for a specific question from the specified assistant.
     *
     * @param name     the name of the assistant
     * @param question the question to retrieve an answer for
     * @return the answer if found, or null if assistant or answer not found
     */
    String getAnswer(String name, String question);

}
