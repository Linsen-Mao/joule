package com.ls.mao.joule.util;

import com.ls.mao.joule.model.AssistantPdf;
import com.ls.mao.joule.repo.AssistantPdfRepository;
import com.ls.mao.joule.util.IngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.NoSuchElementException;

@Service
public class PdfService {

    private static final Logger log = LoggerFactory.getLogger(PdfService.class);
    private final AssistantPdfRepository assistantPdfRepository;
    private final IngestionService ingestionService;

    @Value("${file.upload-dir}")
    private String pdfUploadDir;

    public PdfService(AssistantPdfRepository assistantPdfRepository, IngestionService ingestionService) {
        this.assistantPdfRepository = assistantPdfRepository;
        this.ingestionService = ingestionService;
    }

    @Transactional
    public String uploadPdf(String assistantName, MultipartFile pdfFile) {
        try {
            String fileName = pdfFile.getOriginalFilename();

            File uploadDir = new File(pdfUploadDir);
            if (!uploadDir.exists() && !uploadDir.mkdirs()) {
                throw new IOException("Failed to create upload directory: " + pdfUploadDir);
            }

            File pdfPath = new File(uploadDir, fileName);
            log.info("Uploading file to path: {}", pdfPath.getAbsolutePath());
            Files.copy(pdfFile.getInputStream(), pdfPath.toPath(), StandardCopyOption.REPLACE_EXISTING);

            AssistantPdf assistantPdf = new AssistantPdf(assistantName, fileName);
            assistantPdfRepository.save(assistantPdf);

            ingestionService.ingestPdfFile(assistantName, fileName);

            return "PDF file uploaded successfully for assistant: " + assistantName;

        } catch (IOException e) {
            log.error("File upload failed: {}", e.getMessage(), e);
            throw new RuntimeException("File upload failed. Please try again.", e);
        }
    }
}
