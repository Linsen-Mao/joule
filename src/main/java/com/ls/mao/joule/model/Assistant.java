package com.ls.mao.joule.model;

public class Assistant {
    private String name;
    private String defaultResponse;

    public Assistant(String name, String defaultResponse) {
        this.name = name;
        this.defaultResponse = defaultResponse;
    }

    public String getName() {
        return name;
    }

    public String getDefaultResponse() {
        return defaultResponse;
    }
}
