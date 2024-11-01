package com.ls.mao.joule.controller;

import com.ls.mao.joule.event.PdfUploadRequestEvent;
import com.ls.mao.joule.model.*;
import com.ls.mao.joule.service.AssistantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/assistant")
public class AssistantController {

    private static final Logger logger = LoggerFactory.getLogger(AssistantController.class);

    private final AssistantService assistantService;
    private final ApplicationEventPublisher eventPublisher;

    public AssistantController(AssistantService assistantService, ApplicationEventPublisher eventPublisher) {
        this.assistantService = assistantService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Registers a new assistant by saving the assistant's name and initial response.
     * @param request
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerAssistant(@RequestBody RegisterAssistantRequest request) {
        logger.info("Registering assistant with name: {}", request.name());
        String result = assistantService.registerAssistant(request.name(), request.response(),request.systemPrompt());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * retrieves the stored response for a specified assistant by name
     * @param name
     * @return
     */
    @GetMapping("/{name}")
    public ResponseEntity<ApiResponse> getAssistantResponse(@PathVariable String name) {
        String response = assistantService.getResponse(name);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    /**
     * Retrieves an answer for a question for a specified assistant.
     * @param name
     * @param request
     * @return
     */

    @PostMapping("/{name}/answer")
    public ResponseEntity<ApiResponse> getAnswer(@PathVariable String name, @RequestBody QuestionRequest request) {
        String answer = assistantService.getAnswer(name, request.question());
        return ResponseEntity.ok(ApiResponse.success(answer));
    }

    /**
     * Uploads pdf for specific assistant
     * @param name
     * @param file
     * @return
     */
    @PostMapping("/{name}/upload")
    public ResponseEntity<ApiResponse> uploadPdf(
            @PathVariable String name,
            @RequestParam("file") MultipartFile file) {

        Assistant assistant = assistantService.getAssistant(name);
        logger.debug("Publishing PDF upload event for assistant: {}", assistant.getName());
        eventPublisher.publishEvent(new PdfUploadRequestEvent(this, name, file));
        return ResponseEntity.ok(ApiResponse.success("PDF upload event published for assistant: " + name));
    }
}
