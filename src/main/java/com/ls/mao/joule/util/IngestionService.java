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
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class IngestionService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(IngestionService.class);
    private final VectorStore vectorStore;

    @Value("classpath:/docs/SAP.pdf")
    private Resource marketPDF;

    @Value("${spring.ai.vectorstore.pgvector.reingest-on-start}")
    private boolean reingestOnStart;

    public IngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!reingestOnStart) {
            log.info("Embeddings already exist in VectorStore. Skipping ingestion.");
            return;
        }

        var pdfReader = new PagePdfDocumentReader(marketPDF);
        TextSplitter textSplitter = new TokenTextSplitter();
        List<Document> sanitizedDocuments = pdfReader.get().stream()
                .map(document -> new Document(document.getContent().replaceAll("\u0000", ""), document.getMetadata()))
                .collect(Collectors.toList());
        vectorStore.accept(textSplitter.apply(sanitizedDocuments));
        log.info("VectorStore Loaded with data!");
    }
}
