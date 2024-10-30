package com.ls.mao.joule.repo;

import com.ls.mao.joule.model.Assistant;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class AssistantRepository {
    private final Map<String, Assistant> assistants = new HashMap<>();

    public Assistant save(Assistant assistant) {
        assistants.put(assistant.getName(), assistant);
        return assistant;
    }

    public Assistant findByName(String name) {
        return assistants.get(name);
    }
}
