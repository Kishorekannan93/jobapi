package com.jobai.service;
import com.jobai.entity.Company;
import com.jobai.entity.User;
import com.jobai.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Service @RequiredArgsConstructor @Slf4j
public class ExcelService {
    private final CompanyRepository companyRepository;
    public List<Company> parseExcel(MultipartFile file, User user) throws IOException {
        List<Company> companies = new ArrayList<>();
        log.info("Parsing Excel file: {}", file.getOriginalFilename());
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String companyName = getCellValue(row.getCell(0));
                String email = getCellValue(row.getCell(1));
                String jobTitle = getCellValue(row.getCell(2));
                String jobDescription = getCellValue(row.getCell(3));
                if (companyName.isEmpty() || email.isEmpty()) { log.warn("Skipping row {} - missing data", i); continue; }
                Company company = new Company();
                company.setUser(user);
                company.setCompanyName(companyName);
                company.setEmail(email);
                company.setJobTitle(jobTitle);
                company.setJobDescription(jobDescription);
                company.setStatus("pending");
                companies.add(company);
            }
        }
        List<Company> saved = companyRepository.saveAll(companies);
        log.info("Saved {} companies to database", saved.size());
        return saved;
    }
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue().trim();
            case NUMERIC: return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return "";
        }
    }
}