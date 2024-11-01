package com.ls.mao.joule.model;


import jakarta.persistence.*;

@Entity
public class Assistant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String defaultResponse;

    @Column(nullable = false)
    private String systemPrompt;

    public Assistant(String name, String defaultResponse, String systemPrompt) {
        this.name = name;
        this.defaultResponse = defaultResponse;
        this.systemPrompt = systemPrompt;
    }


    public Assistant() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultResponse() {
        return defaultResponse;
    }

    public void setDefaultResponse(String defaultResponse) {
        this.defaultResponse = defaultResponse;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

}
