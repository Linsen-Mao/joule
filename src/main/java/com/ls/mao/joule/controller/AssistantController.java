package com.ls.mao.joule.controller;

import com.ls.mao.joule.model.*;
import com.ls.mao.joule.service.AssistantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assistant")
public class AssistantController {

    private static final Logger logger = LoggerFactory.getLogger(AssistantController.class);

    private final AssistantService assistantService;

    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    /**
     * Registers a new assistant by saving the assistant's name and initial response.
     * @param request
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerAssistant(@RequestBody RegisterAssistantRequest request) {
        logger.info("Registering assistant with name: {}", request.name());
        String result = assistantService.registerAssistant(request.name(), request.response());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * retrieves the stored response for a specified assistant by name
     * @param name
     * @return
     */
    @GetMapping("/{name}")
    public ResponseEntity<ApiResponse> getAssistantResponse(@PathVariable("name") String name) {
        if (!StringUtils.hasText(name)) {
            logger.warn("Assistant name is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Assistant name must not be null or empty"));
        }

        logger.info("Fetching response for assistant with name: {}", name);
        String response = assistantService.getResponse(name);

        if (response == null) {
            logger.warn("No response found for assistant: {}", name);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("No response found for assistant: " + name));
        }

        return ResponseEntity.ok(ApiResponse.success(response));
    }


    /**
     * Retrieves an answer for a question for a specified assistant.
     * @param name
     * @param request
     * @return
     */
    @PostMapping("/{name}/answer")
    public ResponseEntity<ApiResponse> getAnswer(
            @PathVariable("name") String name,
            @RequestBody QuestionRequest request) {

        String answer = assistantService.getAnswer(name, request.question());

        if (answer == null || answer.isEmpty()) {
            logger.warn("No answer found for assistant: {} and question: {}", name, request.question());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("No answer found for the given question"));
        }

        return ResponseEntity.ok(ApiResponse.success(answer));
    }
}
