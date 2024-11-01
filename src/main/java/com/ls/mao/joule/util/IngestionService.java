package com.ls.mao.joule.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class IngestionService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(IngestionService.class);
    private final VectorStore vectorStore;
    private final IngestionPolicy ingestionPolicy;

    @Value("file:src/main/resources/docs/*.pdf")
    private String marketDocumentsPath;

    public IngestionService(VectorStore vectorStore, IngestionPolicy ingestionPolicy) {
        this.vectorStore = vectorStore;
        this.ingestionPolicy = ingestionPolicy;
    }

    @Override
    public void run(String... args) {
        if (!ingestionPolicy.shouldIngest()) {
            return;
        }
        processDocuments();
    }

    private void processDocuments() {
        try {
            loadDocumentsInBatches(marketDocumentsPath, 100)
                    .forEach(batch -> {
                        List<Document> sanitizedBatch = batch.stream()
                                .map(document -> new Document(document.getContent().replaceAll("\u0000", ""), document.getMetadata()))
                                .collect(Collectors.toList());
                        vectorStore.accept(new TokenTextSplitter().apply(sanitizedBatch));
                    });
            logger.info("VectorStore loaded with data successfully!");
        } catch (Exception e) {
            logger.error("An error occurred during document ingestion", e);
        }
    }

    private List<List<Document>> loadDocumentsInBatches(String path, int batchSize) throws IOException {
        List<Document> documents = loadDocuments(path);
        List<List<Document>> batches = new ArrayList<>();
        for (int i = 0; i < documents.size(); i += batchSize) {
            batches.add(documents.subList(i, Math.min(i + batchSize, documents.size())));
        }
        return batches;
    }

    private List<Document> loadDocuments(String path) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        return Arrays.stream(resolver.getResources(path))
                .flatMap(resource -> {
                    try {
                        return new PagePdfDocumentReader(resource).get().stream();
                    } catch (Exception e) {
                        logger.error("Error reading PDF file: {}", resource.getFilename(), e);
                        return Stream.empty();
                    }
                })
                .collect(Collectors.toList());
    }
}
