package com.ls.mao.joule.model;

public record RegisterAssistantRequest(String name, String response, String systemPrompt) {

    public RegisterAssistantRequest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        if (response == null || response.isBlank()) {
            throw new IllegalArgumentException("Response cannot be null or blank");
        }
    }
}
