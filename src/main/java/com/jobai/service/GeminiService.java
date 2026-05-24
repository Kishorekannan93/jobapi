package com.jobai.service;

import com.jobai.config.GeminiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {

    private final GeminiConfig geminiConfig;
    private final RestTemplate restTemplate;

    @Value("${gemini.api.url}") private String geminiApiUrl;

    public String generateEmail(String resumeContent, String jobDescription, String companyName, String jobTitle, String userName, String linkedin, String portfolio) {
        String links = "";
        if (linkedin != null && !linkedin.isEmpty()) links += "LinkedIn: " + linkedin + "\n";
        if (portfolio != null && !portfolio.isEmpty()) links += "Portfolio: " + portfolio + "\n";

        String prompt = String.format(
            "You are an expert job application assistant. Create a professional, personalized job application email.\n" +
            "APPLICANT NAME: %s\n" +
            "MY RESUME CONTENT:\n%s\n" +
            "JOB DESCRIPTION:\n%s\n" +
            "COMPANY NAME: %s\n" +
            "JOB TITLE: %s\n" +
            "%s\n" +
            "Instructions:\n" +
            "1. Write a compelling, personalized subject line\n" +
            "2. Start with a professional greeting\n" +
            "3. Mention 2-3 specific skills from my resume that directly match the job description\n" +
            "4. Show genuine enthusiasm for the role and company\n" +
            "5. Include a strong call to action requesting an interview\n" +
            "6. End with a professional closing\n" +
            "7. Include my contact links only if provided above (LinkedIn/Portfolio)\n" +
            "IMPORTANT: Do NOT use markdown formatting like **bold** or *italic*. Use plain text only.\n" +
            "Format your response EXACTLY as:\n" +
            "SUBJECT: [Your generated subject line here]\n" +
            "BODY:\n" +
            "[Your complete email body here with proper paragraphs]\n" +
            "Make it sound natural, confident, and not overly formal.",
            userName, truncateText(resumeContent, 3000), truncateText(jobDescription, 2000), companyName, jobTitle, links);

        String response = callGeminiAPI(prompt);
        return stripMarkdown(response);
    }

    public String chatWithAI(String userMessage, String context) {
        String prompt = String.format(
            "You are a friendly, helpful AI Job Application Assistant.\n" +
            "Context: %s\n" +
            "User: %s\n" +
            "Respond helpfully in plain text only. Do NOT use markdown formatting like **bold** or *italic*.\n" +
            "If user wants to send emails, guide them to upload resume and Excel first.",
            context, userMessage);

        String response = callGeminiAPI(prompt);
        return stripMarkdown(response);
    }

    public String analyzeResume(String resumeContent) {
        String prompt = String.format(
            "Analyze this resume and provide constructive feedback:\n" +
            "RESUME:\n%s\n" +
            "Provide: 1. Top 3 strengths 2. Top 3 areas for improvement 3. Suggested skills to add 4. ATS optimization tips\n" +
            "Use plain text only. Do NOT use markdown formatting like **bold** or *italic*.",
            truncateText(resumeContent, 4000));

        String response = callGeminiAPI(prompt);
        return stripMarkdown(response);
    }

    private String callGeminiAPI(String prompt) {
        try {
            String apiKey = geminiConfig.getApiKey();
            if (apiKey == null || apiKey.isEmpty() || apiKey.equals("YOUR_GEMINI_API_KEY_HERE")) {
                return "ERROR: Gemini API key not configured. Get free key: https://aistudio.google.com/app/apikey";
            }

            String url = geminiApiUrl + "?key=" + apiKey;

            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);
            content.put("parts", List.of(part));
            requestBody.put("contents", List.of(content));

            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("maxOutputTokens", 2048);
            requestBody.put("generationConfig", generationConfig);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getBody() != null) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> content1 = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content1.get("parts");
                    return (String) parts.get(0).get("text");
                }
            }
            return "Sorry, I could not generate a response. Please try again.";
        } catch (Exception e) {
            log.error("Gemini API Error: {}", e.getMessage());
            return "Error calling AI service: " + e.getMessage() + ". Check your API key and internet connection.";
        }
    }

    private String stripMarkdown(String text) {
        if (text == null) return "";
        return text
            .replaceAll("\\*\\*(.*?)\\*\\*", "$1")     // **bold** -> bold
            .replaceAll("\\*(.*?)\\*", "$1")           // *italic* -> italic
            .replaceAll("`{3}[\\s\\S]*?`{3}", "")      // ```code blocks``` -> remove
            .replaceAll("`([^`]*)`", "$1")            // `code` -> code
            .replaceAll("#+\\s+", "")                 // # headers -> remove
            .replaceAll("\\[([^\\]]*)]\\([^)]*\\)", "$1") // [link](url) -> link text
            .replaceAll(">\\s+", "")                  // > quote -> remove
            .trim();
    }

    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
}