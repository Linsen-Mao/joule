package com.ls.mao.joule.model;

import java.util.HashMap;
import java.util.Map;

public class Assistant {
    private String name;
    private String defaultResponse;
    private Map<String, String> questionAnswerPairs;

    public Assistant(String name, String defaultResponse) {
        this.name = name;
        this.defaultResponse = defaultResponse;
        this.questionAnswerPairs = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getDefaultResponse() {
        return defaultResponse;
    }

    //TODO
    public Map<String, String> getQuestionAnswerPairs() {
        return questionAnswerPairs;
    }

    //TODO
    public void addQuestionAnswer(String question, String answer) {
        questionAnswerPairs.put(question, answer);
    }

    //TODO
    public String getAnswer(String question) {
        return questionAnswerPairs.getOrDefault(question, defaultResponse);
    }
}
