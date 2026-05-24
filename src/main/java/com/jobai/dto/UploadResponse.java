package com.jobai.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data @AllArgsConstructor
public class UploadResponse { private boolean success; private String message; private int count; }