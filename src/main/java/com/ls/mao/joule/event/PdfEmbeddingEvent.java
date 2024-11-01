package com.ls.mao.joule.event;

import org.springframework.context.ApplicationEvent;

public class PdfEmbeddingEvent extends ApplicationEvent {

    private final String assistantName;
    private final String pdfFileName;

    public PdfEmbeddingEvent(Object source, String assistantName, String pdfFileName) {
        super(source);
        this.assistantName = assistantName;
        this.pdfFileName = pdfFileName;
    }

    public String getAssistantName() {
        return assistantName;
    }

    public String getPdfFileName() {
        return pdfFileName;
    }
}
