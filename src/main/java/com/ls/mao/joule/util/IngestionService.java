package com.ls.mao.joule.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class IngestionService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(IngestionService.class);
    private final VectorStore vectorStore;

    @Value("file:src/main/resources/docs/*.pdf")
    private String marketPDFsPath;

    @Value("${spring.ai.vectorstore.pgvector.reingest-on-start}")
    private boolean reingestOnStart;

    @Value("${spring.ai.vectorstore.pgvector.activated}")
    private boolean activated;

    public IngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(String... args) throws Exception {
        if(!activated){
            log.info("RAG is inactivated");
            return;
        }

        if (!reingestOnStart) {
            log.info("Embeddings already exist in VectorStore. Skipping ingestion.");
            return;
        }

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] marketPDFs;
        try {
            marketPDFs = resolver.getResources(marketPDFsPath);
        } catch (Exception e) {
            log.error("Failed to load resources from path: " + marketPDFsPath, e);
            return;
        }

        if (marketPDFs == null || marketPDFs.length == 0) {
            log.info("No PDF files found for ingestion. Skipping embedding.");
            return;
        }

        TextSplitter textSplitter = new TokenTextSplitter();
        List<Document> sanitizedDocuments = Arrays.stream(marketPDFs)
                .flatMap(resource -> {
                    try {
                        var pdfReader = new PagePdfDocumentReader(resource);
                        return pdfReader.get().stream()
                                .map(document -> new Document(document.getContent().replaceAll("\u0000", ""), document.getMetadata()));
                    } catch (Exception e) {
                        log.error("Error reading PDF file: " + resource.getFilename(), e);
                        return null;
                    }
                })
                .filter(doc -> doc != null)
                .collect(Collectors.toList());

        if (sanitizedDocuments.isEmpty()) {
            log.info("No valid documents found after processing PDFs. Skipping embedding.");
            return;
        }

        vectorStore.accept(textSplitter.apply(sanitizedDocuments));
        log.info("VectorStore Loaded with data!");
    }
}
