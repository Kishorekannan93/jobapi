package com.jobai.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data @AllArgsConstructor
public class BulkEmailResponse { private String companyName; private String status; private String message; }