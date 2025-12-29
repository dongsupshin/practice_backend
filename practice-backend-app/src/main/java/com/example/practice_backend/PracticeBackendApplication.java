package com.example.practice_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.practice_backend", "com.example.helloworld"})
public class PracticeBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PracticeBackendApplication.class, args);
    }

}

