package com.jobai.service;
import com.jobai.dto.AuthResponse;
import com.jobai.dto.LoginRequest;
import com.jobai.dto.RegisterRequest;
import com.jobai.entity.User;
import com.jobai.repository.UserRepository;
import com.jobai.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service @RequiredArgsConstructor @Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return new AuthResponse(null, null, null, "Email already registered");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setGmailAppPassword(request.getGmailAppPassword());
        user.setPhone(request.getPhone());
        user.setLinkedinUrl(request.getLinkedinUrl());
        user.setPortfolioUrl(request.getPortfolioUrl());
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getName(), "Registered successfully");
    }
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new AuthResponse(null, null, null, "Invalid email or password");
        }
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getName(), "Login successful");
    }
    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}