package com.ls.mao.joule.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class IngestionService {

    private static final Logger logger = LoggerFactory.getLogger(IngestionService.class);
    private final VectorStore vectorStore;

    @Value("${file.upload-dir}")
    private String pdfUploadDirectory;

    public IngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }


    public void ingestPdfFile(String assistantName, String pdfFileName) {
        try {
            File pdfFile = new File(pdfUploadDirectory, pdfFileName);
            Resource resource = new UrlResource(pdfFile.toURI());

            if (resource.exists() && resource.isReadable()) {
                List<Document> documents = new PagePdfDocumentReader(resource).get().stream()
                        .map(document -> {
                            document = new Document(document.getContent().replaceAll("\u0000", ""), document.getMetadata());
                            Map<String, Object> metadata = document.getMetadata();
                            metadata.put("assistant", assistantName);
                            return new Document(document.getContent(), metadata);
                        })
                        .collect(Collectors.toList());

                vectorStore.accept(new TokenTextSplitter().apply(documents));
                logger.info("Ingested PDF '{}' for assistant '{}'", pdfFileName, assistantName);
            } else {
                logger.warn("PDF '{}' for assistant '{}' could not be read", pdfFileName, assistantName);
            }
        } catch (IOException e) {
            logger.error("Failed to ingest PDF '{}' for assistant '{}'", pdfFileName, assistantName, e);
        }
    }
}