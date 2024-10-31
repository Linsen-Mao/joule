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

    // Constructor
    public Assistant(String name, String defaultResponse) {
        this.name = name;
        this.defaultResponse = defaultResponse;
    }

    public Assistant() {
        // Default constructor for JPA
    }

    // Getters and setters
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
}
