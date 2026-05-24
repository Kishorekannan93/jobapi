package com.jobai.controller;
import com.jobai.dto.UploadResponse;
import com.jobai.entity.Company;
import com.jobai.entity.User;
import com.jobai.service.AuthService;
import com.jobai.service.ExcelService;
import com.jobai.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
@RestController @RequestMapping("/api/companies") @RequiredArgsConstructor @Slf4j
public class CompanyController {
    private final ExcelService excelService;
    private final EmailService emailService;
    private final AuthService authService;
    @PostMapping("/upload-excel") public ResponseEntity<UploadResponse> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = authService.getCurrentUser(email);
            List<Company> companies = excelService.parseExcel(file, user);
            return ResponseEntity.ok(new UploadResponse(true, "Imported successfully", companies.size()));
        } catch (Exception e) { return ResponseEntity.badRequest().body(new UploadResponse(false, "Failed: " + e.getMessage(), 0)); }
    }
    @GetMapping("/all") public ResponseEntity<List<Company>> getAllCompanies() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = authService.getCurrentUser(email);
        return ResponseEntity.ok(emailService.getAllCompanies(user.getId()));
    }
    @GetMapping("/pending") public ResponseEntity<List<Company>> getPendingCompanies() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = authService.getCurrentUser(email);
        return ResponseEntity.ok(emailService.getPendingCompanies(user.getId()));
    }
}