package com.ls.mao.joule.repo;

import com.ls.mao.joule.model.Assistant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssistantRepository extends JpaRepository<Assistant, Long> {
    Assistant findByName(String name);
}