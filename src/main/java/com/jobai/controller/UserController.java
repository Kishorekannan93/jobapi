package com.jobai.controller;
import com.jobai.entity.User;
import com.jobai.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/user") @RequiredArgsConstructor
public class UserController {
    private final AuthService authService;
    @GetMapping("/profile") public ResponseEntity<User> getProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(authService.getCurrentUser(email));
    }
}