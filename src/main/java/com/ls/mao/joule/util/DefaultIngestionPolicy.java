package com.ls.mao.joule.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DefaultIngestionPolicy implements IngestionPolicy {

    private static final Logger logger = LoggerFactory.getLogger(DefaultIngestionPolicy.class);

    @Value("${spring.ai.vectorstore.pgvector.activated}")
    private boolean isPgVectorActivated;

    @Value("${spring.ai.vectorstore.pgvector.reingest-on-start}")
    private boolean reingestOnStartup;

    @Override
    public boolean shouldIngest() {
        if (!isPgVectorActivated) {
            logger.info("VectorStore ingestion is deactivated");
            return false;
        }
        if (!reingestOnStartup) {
            logger.info("Embeddings already exist in VectorStore. Skipping ingestion.");
            return false;
        }
        return true;
    }
}
