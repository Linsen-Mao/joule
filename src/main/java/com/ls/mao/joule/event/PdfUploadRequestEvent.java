package com.ls.mao.joule.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.web.multipart.MultipartFile;

public class PdfUploadRequestEvent extends ApplicationEvent {
    private final String assistantName;
    private final MultipartFile pdfFile;

    public PdfUploadRequestEvent(Object source, String assistantName, MultipartFile pdfFile) {
        super(source);
        this.assistantName = assistantName;
        this.pdfFile = pdfFile;
    }

    public String getAssistantName() {
        return assistantName;
    }

    public MultipartFile getPdfFile() {
        return pdfFile;
    }
}
