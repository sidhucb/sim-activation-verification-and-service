package com.example.documentverification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication
public class DocumentVerificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DocumentVerificationServiceApplication.class, args);
    }
}
