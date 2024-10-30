package com.ls.mao.joule.controller;

import com.ls.mao.joule.model.*;
import com.ls.mao.joule.service.AssistantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * Adds a question-answer pair for a specified assistant.
     * Note: This method will be replaced by Spring AI in the future.
     * @param name
     * @param request
     * @return
     */
    //TODO
    @PostMapping("/{name}/addQnA")
    public ResponseEntity<ApiResponse> addQuestionAnswer(
            @PathVariable("name") String name,
            @RequestBody AddQnARequest request) {

        logger.info("Adding QnA for assistant: {}", name);
        String result = assistantService.addQuestionAnswer(name, request.question(), request.answer());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Retrieves an answer for a question for a specified assistant.
     * Note: This method will be replaced by Spring AI in the future.
     * @param name
     * @param request
     * @param fallback
     * @return
     */
    //TODO
    @PostMapping("/{name}/answer")
    public ResponseEntity<ApiResponse> getAnswer(
            @PathVariable("name") String name,
            @RequestBody QuestionRequest request,
            @RequestParam(value = "fallback", required = false, defaultValue = "false") boolean fallback) {

        String answer = assistantService.getAnswer(name, request.question());

        if (answer == null && fallback) {
            return ResponseEntity.ok(ApiResponse.success("Sorry, I don't have an answer for that."));
        } else if (answer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("No answer found for the question: " + request.question()));
        }

        return ResponseEntity.ok(ApiResponse.success(answer));
    }
}
