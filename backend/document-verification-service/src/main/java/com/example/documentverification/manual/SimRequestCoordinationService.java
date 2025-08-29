package com.example.documentverification.manual;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.documentverification.DocumentRepository;

@Service
public class SimRequestCoordinationService {

    private final DocumentRepository documentRepository;
    private final UserDocumentManualRepository manualRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public SimRequestCoordinationService(DocumentRepository documentRepository,
                                         UserDocumentManualRepository manualRepository,
                                         RestTemplate restTemplate) {
        this.documentRepository = documentRepository;
        this.manualRepository = manualRepository;
        this.restTemplate = restTemplate;
    }

    public void checkAndCreateSimRequestForUser(Long userId, String adminEmail) {
        boolean isOcrApproved = documentRepository.findByUserIdAndStatus(userId, "approved").size() > 0;
        boolean isManualApproved = manualRepository.findByUserIdAndStatus(userId, "approved").size() > 0;

        if (isOcrApproved && isManualApproved) {
            createSimRequestInSimApp(userId, adminEmail, "Approved");
        }
        // Add logic to handle rejection or partial approvals if needed
    }

    private void createSimRequestInSimApp(Long userId, String userEmail, String status) {
        String simappUrl = "http://localhost:8086/api/sim/requests";
        Map<String, Object> payload = new HashMap<>();

        payload.put("requestId", "REQ-" + System.currentTimeMillis());
        payload.put("userId", userId);
        payload.put("email", userEmail);
        String username = userEmail.contains("@") ? userEmail.split("@")[0] : userEmail;
        payload.put("username", username);
        payload.put("status", status);

        try {
            restTemplate.postForEntity(simappUrl, payload, String.class);
        } catch (Exception e) {
            System.err.println("Failed to create SIM request for userId " + userId + ": " + e.getMessage());
        }
    }
}

