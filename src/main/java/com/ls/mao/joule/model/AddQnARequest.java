package com.ls.mao.joule.model;

public record AddQnARequest(String question, String answer) {

    public AddQnARequest {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Question cannot be null or blank");
        }
        if (answer == null || answer.isBlank()) {
            throw new IllegalArgumentException("Answer cannot be null or blank");
        }
    }
}
