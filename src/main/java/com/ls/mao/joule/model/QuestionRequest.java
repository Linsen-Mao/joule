package com.ls.mao.joule.model;

public record QuestionRequest(String question) {

    public QuestionRequest {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Question cannot be null or blank");
        }
    }
}
