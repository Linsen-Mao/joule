
package com.ls.mao.joule.model;


import jakarta.persistence.*;

@Entity
@Table(name = "assistant_pdf", uniqueConstraints = @UniqueConstraint(columnNames = {"assistant_name", "pdf_file_name"}))
public class AssistantPdf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assistant_name", nullable = false)
    private String assistantName;

    @Column(name = "pdf_file_name", nullable = false)
    private String pdfFileName;

    public AssistantPdf() {}

    public AssistantPdf(String assistantName, String pdfFileName) {
        this.assistantName = assistantName;
        this.pdfFileName = pdfFileName;
    }

    public Long getId() {
        return id;
    }

    public String getAssistantName() {
        return assistantName;
    }

    public void setAssistantName(String assistantName) {
        this.assistantName = assistantName;
    }

    public String getPdfFileName() {
        return pdfFileName;
    }

    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }
}
