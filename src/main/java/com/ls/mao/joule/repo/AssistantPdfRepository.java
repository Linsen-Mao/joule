package com.ls.mao.joule.repo;

import com.ls.mao.joule.model.AssistantPdf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AssistantPdfRepository extends JpaRepository<AssistantPdf, Long> {
}
