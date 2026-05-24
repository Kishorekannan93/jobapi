package com.jobai.controller;
import com.jobai.dto.ChatResponse;
import com.jobai.dto.EmailRequest;
import com.jobai.service.GeminiService;
import com.jobai.service.ResumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/chat") @RequiredArgsConstructor @Slf4j
public class ChatBotController {
    private final GeminiService geminiService;
    private final ResumeService resumeService;
    @PostMapping("/message") public ResponseEntity<ChatResponse> chat(@RequestBody EmailRequest request) { String context = String.format("Resume: %s", resumeService.isResumeLoaded()); return ResponseEntity.ok(new ChatResponse(geminiService.chatWithAI(request.getMessage(), context), "chat")); }
    @PostMapping("/analyze-resume") public ResponseEntity<ChatResponse> analyzeResume() { if (!resumeService.isResumeLoaded()) return ResponseEntity.ok(new ChatResponse("No resume uploaded. Go to Upload screen first.", "error")); return ResponseEntity.ok(new ChatResponse(geminiService.analyzeResume(resumeService.getCachedResume()), "analysis")); }
}