package com.ls.mao.joule.util;

import com.ls.mao.joule.event.PdfEmbeddingEvent;
import com.ls.mao.joule.model.AssistantPdf;
import com.ls.mao.joule.repo.AssistantPdfRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import com.ls.mao.joule.event.PdfUploadRequestEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

@Service
public class PdfService {

    private static final Logger log = LoggerFactory.getLogger(PdfService.class);
    private final AssistantPdfRepository assistantPdfRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${file.upload-dir}")
    private String pdfUploadDir;

    public PdfService(AssistantPdfRepository assistantPdfRepository, ApplicationEventPublisher eventPublisher) {
        this.assistantPdfRepository = assistantPdfRepository;
        this.eventPublisher = eventPublisher;
    }

    @Async
    @EventListener
    public void handlePdfUploadRequest(PdfUploadRequestEvent event) {
        String assistantName = event.getAssistantName();
        MultipartFile pdfFile = event.getPdfFile();

        String fileName = pdfFile.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("Invalid file name.");
        }

        File uploadDir = new File(pdfUploadDir);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new RuntimeException("Failed to create upload directory: " + pdfUploadDir);
        }

        File pdfPath = new File(uploadDir, fileName);
        try {
            log.info("Uploading file to path: {}", pdfPath.getAbsolutePath());
            Files.copy(pdfFile.getInputStream(), pdfPath.toPath(), StandardCopyOption.REPLACE_EXISTING);

            AssistantPdf assistantPdf = new AssistantPdf(assistantName, fileName);
            assistantPdfRepository.save(assistantPdf);

            eventPublisher.publishEvent(new PdfEmbeddingEvent(this, assistantName, fileName));

        } catch (IOException e) {
            log.error("File upload failed: {}", e.getMessage(), e);
            throw new RuntimeException("File upload failed. Please try again.", e);
        }
    }
}
