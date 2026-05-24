package com.jobai.service;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
@Service @Slf4j
public class ResumeService {
    private String cachedResumeContent = "";
    private String cachedFileName = "";
    public String extractTextFromPdf(MultipartFile file) throws IOException {
        log.info("Extracting text from PDF: {}", file.getOriginalFilename());
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            cachedResumeContent = stripper.getText(document);
            cachedFileName = file.getOriginalFilename();
            log.info("Extracted {} characters", cachedResumeContent.length());
            return cachedResumeContent;
        }
    }
    public String getCachedResume() { return cachedResumeContent; }
    public String getCachedFileName() { return cachedFileName; }
    public boolean isResumeLoaded() { return !cachedResumeContent.isEmpty(); }
    public void clearCache() { cachedResumeContent = ""; cachedFileName = ""; }
}