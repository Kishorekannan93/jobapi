// package com.jobai.service;

// import com.jobai.dto.BulkEmailResponse;
// import com.jobai.entity.Company;
// import com.jobai.entity.EmailTemplate;
// import com.jobai.entity.User;
// import com.jobai.repository.CompanyRepository;
// import com.jobai.repository.EmailTemplateRepository;
// import jakarta.mail.MessagingException;
// import jakarta.mail.internet.MimeMessage;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.core.io.ByteArrayResource;
// import org.springframework.mail.javamail.JavaMailSenderImpl;
// import org.springframework.mail.javamail.MimeMessageHelper;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import java.io.IOException;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Properties;

// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class EmailService {

//     private final GeminiService geminiService;
//     private final ResumeService resumeService;
//     private final CompanyRepository companyRepository;
//     private final EmailTemplateRepository emailTemplateRepository;

//     public List<BulkEmailResponse> sendBulkEmails(MultipartFile resumeFile, User user) {
//         List<Company> pendingCompanies = companyRepository.findByUserIdAndStatus(user.getId(), "pending");
//         List<BulkEmailResponse> results = new ArrayList<>();

//         log.info("Starting bulk email for {} companies for user: {}", pendingCompanies.size(), user.getEmail());

//         if (pendingCompanies.isEmpty()) {
//             results.add(new BulkEmailResponse("N/A", "INFO", "No pending companies. Upload Excel first."));
//             return results;
//         }

//         if (user.getGmailAppPassword() == null || user.getGmailAppPassword().isEmpty()) {
//             results.add(new BulkEmailResponse("ALL", "FAILED", "Please add your Gmail App Password in Profile settings"));
//             return results;
//         }

//         if (resumeFile == null || resumeFile.isEmpty()) {
//             results.add(new BulkEmailResponse("ALL", "FAILED", "Please upload your resume PDF"));
//             return results;
//         }

//         String resumeContent;
//         try {
//             resumeContent = resumeService.extractTextFromPdf(resumeFile);
//         } catch (IOException e) {
//             log.error("Resume parsing failed: {}", e.getMessage());
//             return List.of(new BulkEmailResponse("ALL", "FAILED", "Resume parsing failed: " + e.getMessage()));
//         }

//         JavaMailSenderImpl mailSender = createMailSender(user.getEmail(), user.getGmailAppPassword());

//         for (Company company : pendingCompanies) {
//             try {
//                 if (!isCompanyDataValid(company)) {
//                     log.warn("Skipping company {} - incomplete data", company.getCompanyName());
//                     company.setStatus("failed");
//                     companyRepository.save(company);
//                     results.add(new BulkEmailResponse(company.getCompanyName(), "FAILED", "Company data incomplete (missing email/title)"));
//                     continue;
//                 }

//                 String aiResponse = geminiService.generateEmail(
//                     resumeContent,
//                     company.getJobDescription(),
//                     company.getCompanyName(),
//                     company.getJobTitle(),
//                     user.getName(),
//                     user.getLinkedinUrl(),
//                     user.getPortfolioUrl()
//                 );

//                 String subject = extractSubject(aiResponse);
//                 String rawBody = extractBody(aiResponse);
//                 String plainBody = stripMarkdown(rawBody);
//                 String htmlBody = markdownToHtml(rawBody);

//                 EmailTemplate template = new EmailTemplate();
//                 template.setCompany(company);
//                 template.setGeneratedSubject(subject);
//                 template.setGeneratedBody(plainBody);
//                 emailTemplateRepository.save(template);

//                 sendEmailWithAttachment(
//                     mailSender,
//                     company.getEmail(),
//                     subject,
//                     htmlBody,
//                     plainBody,
//                     resumeFile,
//                     user.getName(),
//                     user.getEmail()
//                 );

//                 company.setStatus("sent");
//                 company.setSentAt(LocalDateTime.now());
//                 companyRepository.save(company);

//                 results.add(new BulkEmailResponse(company.getCompanyName(), "SUCCESS", "Email sent successfully"));
//                 log.info("Email sent to: {}", company.getEmail());

//                 Thread.sleep(1000);
//             } catch (Exception e) {
//                 log.error("Failed to send to {}: {}", company.getCompanyName(), e.getMessage());
//                 company.setStatus("failed");
//                 companyRepository.save(company);
//                 results.add(new BulkEmailResponse(company.getCompanyName(), "FAILED", e.getMessage()));
//             }
//         }
//         return results;
//     }

//     private boolean isCompanyDataValid(Company company) {
//         return company.getCompanyName() != null && !company.getCompanyName().isEmpty()
//             && company.getEmail() != null && !company.getEmail().isEmpty()
//             && company.getJobTitle() != null && !company.getJobTitle().isEmpty();
//     }

//     private JavaMailSenderImpl createMailSender(String email, String appPassword) {
//         JavaMailSenderImpl sender = new JavaMailSenderImpl();
//         sender.setHost("smtp.gmail.com");
//         sender.setPort(587);
//         sender.setUsername(email);
//         sender.setPassword(appPassword);
//         Properties props = sender.getJavaMailProperties();
//         props.put("mail.smtp.auth", "true");
//         props.put("mail.smtp.starttls.enable", "true");
//         props.put("mail.smtp.connectiontimeout", "5000");
//         props.put("mail.smtp.timeout", "5000");
//         props.put("mail.smtp.writetimeout", "5000");
//         return sender;
//     }

//     private void sendEmailWithAttachment(
//             JavaMailSenderImpl mailSender,
//             String to,
//             String subject,
//             String htmlBody,
//             String plainBody,
//             MultipartFile attachment,
//             String senderName,
//             String fromEmail) throws MessagingException, IOException {

//         MimeMessage message = mailSender.createMimeMessage();
//         MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

//         helper.setFrom(fromEmail, senderName);
//         helper.setTo(to);
//         helper.setSubject(subject);
//         helper.setText(plainBody, htmlBody);
//         helper.addAttachment("Resume.pdf", new ByteArrayResource(attachment.getBytes()));

//         mailSender.send(message);
//     }

//     private String extractSubject(String aiResponse) {
//         if (aiResponse == null || aiResponse.isEmpty()) {
//             return "Application for Job Position";
//         }

//         String[] markers = {"SUBJECT:", "Subject:", "subject:"};
//         String[] bodyMarkers = {"BODY:", "Body:", "body:", "Dear"};

//         for (String marker : markers) {
//             int start = aiResponse.indexOf(marker);
//             if (start != -1) {
//                 start += marker.length();
//                 int end = aiResponse.length();
//                 for (String bodyMarker : bodyMarkers) {
//                     int bodyIndex = aiResponse.indexOf(bodyMarker, start);
//                     if (bodyIndex != -1 && bodyIndex < end) {
//                         end = bodyIndex;
//                     }
//                 }
//                 String subject = aiResponse.substring(start, end).trim();
//                 if (!subject.isEmpty()) return subject;
//             }
//         }
//         return "Application for Job Position";
//     }

//     private String extractBody(String aiResponse) {
//         if (aiResponse == null || aiResponse.isEmpty()) {
//             return aiResponse;
//         }

//         String[] markers = {"BODY:", "Body:", "body:"};
//         for (String marker : markers) {
//             int start = aiResponse.indexOf(marker);
//             if (start != -1) {
//                 return aiResponse.substring(start + marker.length()).trim();
//             }
//         }

//         String[] altMarkers = {"Dear", "Hi", "Hello"};
//         for (String marker : altMarkers) {
//             int start = aiResponse.indexOf(marker);
//             if (start != -1) {
//                 return aiResponse.substring(start).trim();
//             }
//         }

//         return aiResponse;
//     }

//     private String stripMarkdown(String text) {
//         if (text == null) return "";
//         return text
//             .replaceAll("\\*\\*(.*?)\\*\\*", "$1")
//             .replaceAll("\\*(.*?)\\*", "$1")
//             .replaceAll("`{3}[\\s\\S]*?`{3}", "")
//             .replaceAll("`([^`]*)`", "$1")
//             .replaceAll("#+\\s+", "")
//             .replaceAll("\\[([^\\]]*)]\\([^)]*\\)", "$1")
//             .replaceAll(">\\s+", "")
//             .trim();
//     }

//     private String markdownToHtml(String text) {
//         if (text == null) return "";
//         String html = text
//             .replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>")
//             .replaceAll("\\*(.*?)\\*", "<i>$1</i>")
//             .replaceAll("`{3}[\\s\\S]*?`{3}", "<pre>$0</pre>")
//             .replaceAll("`([^`]*)`", "<code>$1</code>")
//             .replaceAll("\\n", "<br>");
//         return "<html><body style='font-family: Arial, sans-serif; font-size: 14px; line-height: 1.6; color: #333;'>" + html + "</body></html>";
//     }

//     public List<Company> getAllCompanies(Long userId) {
//         return companyRepository.findByUserId(userId);
//     }

//     public List<Company> getPendingCompanies(Long userId) {
//         return companyRepository.findByUserIdAndStatus(userId, "pending");
//     }

//     public String getEmailStats(Long userId) {
//         long total = companyRepository.findByUserId(userId).size();
//         long pending = companyRepository.findByUserIdAndStatus(userId, "pending").size();
//         long sent = companyRepository.countByUserIdAndStatus(userId, "sent");
//         long failed = companyRepository.countByUserIdAndStatus(userId, "failed");
//         return String.format("Total: %d | Pending: %d | Sent: %d | Failed: %d", total, pending, sent, failed);
//     }
// }


package com.jobai.service;

import com.jobai.dto.BulkEmailResponse;
import com.jobai.entity.Company;
import com.jobai.entity.EmailTemplate;
import com.jobai.entity.User;
import com.jobai.repository.CompanyRepository;
import com.jobai.repository.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final GeminiService geminiService;
    private final ResumeService resumeService;
    private final CompanyRepository companyRepository;
    private final EmailTemplateRepository emailTemplateRepository;

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${resend.from.email}")
    private String fromEmail;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<BulkEmailResponse> sendBulkEmails(MultipartFile resumeFile, User user) {
        List<Company> pendingCompanies = companyRepository.findByUserIdAndStatus(user.getId(), "pending");
        List<BulkEmailResponse> results = new ArrayList<>();

        log.info("Starting bulk email for {} companies for user: {}", pendingCompanies.size(), user.getEmail());

        if (pendingCompanies.isEmpty()) {
            results.add(new BulkEmailResponse("N/A", "INFO", "No pending companies. Upload Excel first."));
            return results;
        }

        if (resumeFile == null || resumeFile.isEmpty()) {
            results.add(new BulkEmailResponse("ALL", "FAILED", "Please upload your resume PDF"));
            return results;
        }

        String resumeContent;
        try {
            resumeContent = resumeService.extractTextFromPdf(resumeFile);
        } catch (IOException e) {
            log.error("Resume parsing failed: {}", e.getMessage());
            return List.of(new BulkEmailResponse("ALL", "FAILED", "Resume parsing failed: " + e.getMessage()));
        }

        for (Company company : pendingCompanies) {
            try {
                if (!isCompanyDataValid(company)) {
                    log.warn("Skipping company {} - incomplete data", company.getCompanyName());
                    company.setStatus("failed");
                    companyRepository.save(company);
                    results.add(new BulkEmailResponse(company.getCompanyName(), "FAILED", "Company data incomplete"));
                    continue;
                }

                String aiResponse = geminiService.generateEmail(
                    resumeContent,
                    company.getJobDescription(),
                    company.getCompanyName(),
                    company.getJobTitle(),
                    user.getName(),
                    user.getLinkedinUrl(),
                    user.getPortfolioUrl()
                );

                String subject = extractSubject(aiResponse);
                String rawBody = extractBody(aiResponse);
                String plainBody = stripMarkdown(rawBody);
                String htmlBody = markdownToHtml(rawBody);

                EmailTemplate template = new EmailTemplate();
                template.setCompany(company);
                template.setGeneratedSubject(subject);
                template.setGeneratedBody(plainBody);
                emailTemplateRepository.save(template);

                // ✅ Resend HTTP API - No SMTP ports!
                sendEmailViaResendAPI(
                    company.getEmail(),
                    subject,
                    htmlBody,
                    resumeFile,
                    user.getName()
                );

                company.setStatus("sent");
                company.setSentAt(LocalDateTime.now());
                companyRepository.save(company);

                results.add(new BulkEmailResponse(company.getCompanyName(), "SUCCESS", "Email sent successfully"));
                log.info("Email sent to: {}", company.getEmail());

                Thread.sleep(1000);
            } catch (Exception e) {
                log.error("Failed to send to {}: {}", company.getCompanyName(), e.getMessage(), e);
                company.setStatus("failed");
                companyRepository.save(company);
                results.add(new BulkEmailResponse(company.getCompanyName(), "FAILED", e.getMessage()));
            }
        }
        return results;
    }

    private boolean isCompanyDataValid(Company company) {
        return company.getCompanyName() != null && !company.getCompanyName().isEmpty()
            && company.getEmail() != null && !company.getEmail().isEmpty()
            && company.getJobTitle() != null && !company.getJobTitle().isEmpty();
    }

    // ✅ Resend HTTP API - Uses HTTPS (port 443), never blocked!
    private void sendEmailViaResendAPI(String to, String subject, String htmlBody,
                                       MultipartFile attachment, String senderName) throws IOException {
        
        String base64Attachment = Base64.getEncoder().encodeToString(attachment.getBytes());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("from", fromEmail);
        requestBody.put("to", to);
        requestBody.put("subject", subject);
        requestBody.put("html", htmlBody);
        
        // Attachment
        Map<String, String> attach = new HashMap<>();
        attach.put("filename", "Resume.pdf");
        attach.put("content", base64Attachment);
        requestBody.put("attachments", List.of(attach));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(resendApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        String url = "https://api.resend.com/emails";
        
        try {
            Map response = restTemplate.postForObject(url, request, Map.class);
            log.info("Resend API response: {}", response);
        } catch (Exception e) {
            log.error("Resend API error: {}", e.getMessage());
            throw e;
        }
    }

    private String extractSubject(String aiResponse) {
        if (aiResponse == null || aiResponse.isEmpty()) {
            return "Application for Job Position";
        }

        String[] markers = {"SUBJECT:", "Subject:", "subject:"};
        String[] bodyMarkers = {"BODY:", "Body:", "body:", "Dear", "Hi", "Hello"};

        for (String marker : markers) {
            int start = aiResponse.indexOf(marker);
            if (start != -1) {
                start += marker.length();
                int end = aiResponse.length();
                for (String bodyMarker : bodyMarkers) {
                    int bodyIndex = aiResponse.indexOf(bodyMarker, start);
                    if (bodyIndex != -1 && bodyIndex < end) {
                        end = bodyIndex;
                    }
                }
                String subject = aiResponse.substring(start, end).trim();
                if (!subject.isEmpty()) return subject;
            }
        }
        return "Application for Job Position";
    }

    private String extractBody(String aiResponse) {
        if (aiResponse == null || aiResponse.isEmpty()) {
            return aiResponse;
        }

        String[] markers = {"BODY:", "Body:", "body:"};
        for (String marker : markers) {
            int start = aiResponse.indexOf(marker);
            if (start != -1) {
                return aiResponse.substring(start + marker.length()).trim();
            }
        }

        String[] altMarkers = {"Dear", "Hi", "Hello"};
        for (String marker : altMarkers) {
            int start = aiResponse.indexOf(marker);
            if (start != -1) {
                return aiResponse.substring(start).trim();
            }
        }

        return aiResponse;
    }

    private String stripMarkdown(String text) {
        if (text == null) return "";
        return text
            .replaceAll("\\*\\*(.*?)\\*\\*", "$1")
            .replaceAll("\\*(.*?)\\*", "$1")
            .replaceAll("`{3}[\\s\\S]*?`{3}", "")
            .replaceAll("`([^`]*)`", "$1")
            .replaceAll("#+\\s+", "")
            .replaceAll("\\[([^\\]]*)]\\([^)]*\\)", "$1")
            .replaceAll(">\\s+", "")
            .trim();
    }

    private String markdownToHtml(String text) {
        if (text == null) return "";
        String html = text
            .replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>")
            .replaceAll("\\*(.*?)\\*", "<i>$1</i>")
            .replaceAll("`{3}[\\s\\S]*?`{3}", "<pre>$0</pre>")
            .replaceAll("`([^`]*)`", "<code>$1</code>")
            .replaceAll("\\n", "<br>");
        return "<html><body style='font-family: Arial, sans-serif; font-size: 14px; line-height: 1.6; color: #333;'>" + html + "</body></html>";
    }

    public List<Company> getAllCompanies(Long userId) {
        return companyRepository.findByUserId(userId);
    }

    public List<Company> getPendingCompanies(Long userId) {
        return companyRepository.findByUserIdAndStatus(userId, "pending");
    }

    public String getEmailStats(Long userId) {
        long total = companyRepository.findByUserId(userId).size();
        long pending = companyRepository.findByUserIdAndStatus(userId, "pending").size();
        long sent = companyRepository.countByUserIdAndStatus(userId, "sent");
        long failed = companyRepository.countByUserIdAndStatus(userId, "failed");
        return String.format("Total: %d | Pending: %d | Sent: %d | Failed: %d", total, pending, sent, failed);
    }
}