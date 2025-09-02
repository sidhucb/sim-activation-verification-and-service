package com.example.documentverification.manual;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
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
            if (simRequestExists(userId)) {
                System.out.println("SIM request already exists for userId: " + userId + ". Skipping creation.");
                return;
            }

            String userEmail = fetchUserEmail(userId);
            if (userEmail != null) {
                createSimRequestInSimApp(userId, userEmail, "approved");  // Use lowercase for consistency
                sendKycApprovedNotification(userId, userEmail);
            } else {
                System.err.println("User email not found for userId=" + userId + ". Sim request and notification skipped.");
            }
        }
    }

    // Updated: calls simapp microservice API using Eureka service name "sim-service"
    private boolean simRequestExists(Long userId) {
        List<String> statuses = Arrays.asList("pending", "approved", "progress", "provisioning");
        // Use service name "sim-service" registered in Eureka
        String url = "http://simapp/api/sim/requests/exists?userId=" + userId + "&statuses=" + String.join(",", statuses);
        try {
            ResponseEntity<Boolean> response = restTemplate.exchange(url, HttpMethod.GET, null, Boolean.class);
            return Boolean.TRUE.equals(response.getBody());
        } catch (Exception e) {
            System.err.println("Failed to check SIM request existence for userId " + userId + ": " + e.getMessage());
            // Best to assume no existing request if error so workflow is not blocked
            return false;
        }
    }

    // Updated: Use Eureka service name "sim-service"
    private void createSimRequestInSimApp(Long userId, String userEmail, String status) {
        String simappUrl = "http://SIMAPP/api/sim/requests";
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
    
    // Updated: Use Eureka service name "notification-service"
    private void sendKycApprovedNotification(Long userId, String recipientEmail) {
        String url = "http://notification-service/notifications/send";
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("recipientEmail", recipientEmail);
        payload.put("subject", "KYC Approved");
        payload.put("message", "Your KYC has been approved and you can now generate your SIM number.");
        try {
            restTemplate.postForEntity(url, payload, String.class);
        } catch (Exception e) {
            System.err.println("Failed to send KYC approved notification for userId " + userId + ": " + e.getMessage());
        }
    }

    // Updated: Use Eureka service name "user-service"
    private String fetchUserEmail(Long userId) {
        String userServiceUrl = "http://user-service/users/" + userId;
        try {
            Map<String, Object> response = restTemplate.getForObject(userServiceUrl, Map.class);
            if (response != null && response.containsKey("email")) {
                return (String) response.get("email");
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch user email for userId " + userId + ": " + e.getMessage());
        }
        return null;
    }

}
