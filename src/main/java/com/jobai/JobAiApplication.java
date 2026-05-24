package com.jobai;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class JobAiApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobAiApplication.class, args);
        System.out.println("\n==============================================================");
        System.out.println("         JOB AI AUTOMATION V2 - WITH AUTH STARTED");
        System.out.println("==============================================================");
        System.out.println("  NEW Features:");
        System.out.println("  - User Sign Up / Login (JWT)");
        System.out.println("  - User's Gmail as sender");
        System.out.println("  - Profile with LinkedIn/Portfolio");
        System.out.println("  - Better UI & Success Popup");
        System.out.println("==============================================================");
        System.out.println("\n");
    }
}