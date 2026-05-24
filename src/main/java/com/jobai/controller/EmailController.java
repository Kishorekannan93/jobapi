package com.jobai.controller;
import com.jobai.dto.BulkEmailResponse;
import com.jobai.entity.User;
import com.jobai.service.AuthService;
import com.jobai.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
@RestController @RequestMapping("/api/email") @RequiredArgsConstructor @Slf4j
public class EmailController {
    private final EmailService emailService;
    private final AuthService authService;
    @PostMapping("/send-bulk") public ResponseEntity<List<BulkEmailResponse>> sendBulkEmails(@RequestParam("resume") MultipartFile resumeFile) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = authService.getCurrentUser(email);
        return ResponseEntity.ok(emailService.sendBulkEmails(resumeFile, user));
    }
    @GetMapping("/stats") public ResponseEntity<String> getStats() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = authService.getCurrentUser(email);
        return ResponseEntity.ok(emailService.getEmailStats(user.getId()));
    }
}