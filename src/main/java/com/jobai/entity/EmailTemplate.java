package com.jobai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "generated_subject", length = 1000)
    private String generatedSubject;

    @Column(name = "generated_body", length = 5000)
    private String generatedBody;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}