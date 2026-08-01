# 🤖 JobAI Backend

> AI-powered backend service for the JobAI Application, built with Spring Boot, Spring Security, JWT Authentication, PostgreSQL and Google Gemini API. This backend automates resume analysis, ATS scoring, recruiter email generation and job matching through secure REST APIs.

<p align="center">

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-336791?style=for-the-badge&logo=postgresql&logoColor=white)
![Railway](https://img.shields.io/badge/Railway-0B0D0E?style=for-the-badge&logo=railway&logoColor=white)
![Gemini](https://img.shields.io/badge/Google_Gemini_API-4285F4?style=for-the-badge&logo=google&logoColor=white)

</p>

---

# 🌐 Live API

**Backend URL**

```
https://jobapi-production-9bcc.up.railway.app
```

---

# 📖 Overview

JobAI Backend is a secure REST API that powers the JobAI mobile application.

The backend analyzes resumes, evaluates ATS compatibility, compares candidate skills with job descriptions, generates AI-powered recruiter emails and securely manages user authentication.

Designed using a layered Spring Boot architecture, the application emphasizes scalability, security and clean code organization.

---

# 🎯 Project Goal

The objective of this project is to automate the job application workflow using Artificial Intelligence.

Instead of manually checking resumes and writing recruiter emails, JobAI enables candidates to upload their resume, analyze ATS compatibility and generate personalized recruiter emails automatically.

---

# 🚀 Features

## 🔐 Authentication

- JWT Authentication
- Secure Login
- User Registration
- Password Encryption
- Protected APIs

---

## 📄 Resume Analysis

- PDF Resume Parsing
- ATS Score Generation
- Skill Gap Analysis
- Resume Suggestions
- Keyword Matching

---

## 🤖 AI Features

- Google Gemini Integration
- Recruiter Email Generation
- Job Description Analysis
- Personalized AI Suggestions
- Resume Improvement Recommendations

---

## 📧 Email Automation

- Bulk Email Support
- Resend SMTP Integration
- Recruiter Email Generation
- Dynamic Email Templates

---

## ☁ Cloud

- Railway Deployment
- PostgreSQL Database
- REST API Architecture

---

# 🛠 Tech Stack

## Backend

- Java 17
- Spring Boot
- Spring MVC
- Spring Security
- Spring Data JPA
- Hibernate

---

## Authentication

- JWT
- BCrypt

---

## Database

- PostgreSQL

---

## AI & Automation

- Google Gemini API
- Apache PDFBox
- Apache POI
- Resend SMTP

---

## Deployment

- Railway
- GitHub

---

# 📂 Project Structure

```text
src
 ├── controller
 ├── service
 ├── repository
 ├── entity
 ├── dto
 ├── config
 ├── security
 ├── util
 ├── exception
 └── JobApiApplication.java
```

---

# 🔄 Architecture

```text
React Native App

        │

        ▼

Spring Boot REST APIs

        │

JWT Authentication

        │

Business Services

        │

Gemini AI

PDFBox

SMTP

        │

PostgreSQL Database

        │

Railway Cloud
```

---

# 🔗 REST API Modules

## Authentication

- Register User
- Login
- JWT Validation

---

## Resume

- Upload Resume
- Analyze Resume
- ATS Score
- Resume Suggestions

---

## AI

- Generate Recruiter Email
- Analyze Job Description
- Skill Gap Analysis

---

## Email

- Send Email
- Bulk Email

---

# ⚡ Highlights

- Layered Architecture
- Secure JWT Authentication
- AI Powered Resume Analysis
- ATS Score Generation
- Railway Deployment
- PostgreSQL Integration
- Production Ready REST APIs
- Exception Handling
- Validation
- Clean Code Structure

---

# 📸 API Preview

Add screenshots of

- Swagger UI
- Postman Collection
- Login API
- ATS Response
- AI Email Response

---

# 🚀 Getting Started

Clone Repository

```bash
git clone https://github.com/Kishorekannan93/jobapi.git
```

Install

```bash
mvn clean install
```

Run

```bash
mvn spring-boot:run
```

---

# 🔐 Environment Variables

```properties
SPRING_DATASOURCE_URL=

SPRING_DATASOURCE_USERNAME=

SPRING_DATASOURCE_PASSWORD=

JWT_SECRET=

GEMINI_API_KEY=

RESEND_API_KEY=
```

---

# 📈 Future Improvements

- OAuth Login
- Docker Support
- Redis Caching
- AWS Deployment
- CI/CD Pipeline
- Microservices Architecture
- Admin Dashboard
- Analytics

---

# 🔗 Related Repository

### Frontend

https://github.com/Kishorekannan93/jobai_frontend

---

# 👨‍💻 Author

## Kishore Kannan

Software Engineer | Java Full Stack Developer

📧 kishorekannan934@gmail.com

GitHub

https://github.com/Kishorekannan93

Portfolio

https://kishorekannan93.github.io/portfolio

---

⭐ If you like this project, consider giving it a star!
