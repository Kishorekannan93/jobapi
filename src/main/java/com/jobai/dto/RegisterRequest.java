package com.jobai.dto;
import lombok.Data;
@Data
public class RegisterRequest { private String name; private String email; private String password; private String gmailAppPassword; private String phone; private String linkedinUrl; private String portfolioUrl; }