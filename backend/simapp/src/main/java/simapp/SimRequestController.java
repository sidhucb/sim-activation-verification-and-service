package simapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/sim")
@CrossOrigin(origins = "http://localhost:5173")
public class SimRequestController {

    @Autowired
    private SimRequestRepository simRequestRepository;

    @Autowired
    private AllocatedNumberRepository allocatedNumberRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    // Notify user via notification-service
    private void notifyUserWithDetails(Long userId, String email, String subject, String message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("recipientEmail", email);
        payload.put("subject", subject);
        payload.put("message", message);
        restTemplate.postForEntity("http://notification-service/notifications/send", payload, String.class);
    }


    // Extract email from JWT token
    private String getEmailFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.extractEmail(token);
        }
        throw new RuntimeException("Missing or invalid Authorization header");
    }

    // Extract userId from JWT token
    private Long extractUserIdFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.extractId(token);
        }
        throw new RuntimeException("Missing or invalid Authorization header");
    }

    // ---------------- Status Check ----------------
    @PostMapping("/status")
    @Transactional
    public ResponseEntity<StatusCheckResponse> checkRequestStatus(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody StatusCheckRequest req) {

        Long userId = extractUserIdFromToken(authHeader);

        return simRequestRepository.findByUserIdAndRequestId(userId, req.getRequestId())
                .map(r -> {
                    String s = Optional.ofNullable(r.getStatus()).orElse("unknown");
                    String msg;
                    String msisdn = null;

                    switch (s.toLowerCase()) {
                        case "pending" -> msg = "Your application has been submitted and is pending review.";
                        case "approved" -> msg = "KYC approved! Generate and select your number.";
                        case "progress" -> msg = "Please choose a number from the generated list.";
                        case "provisioning" -> msg = "Provisioning in progress. Activation within 24 hours.";
                        case "active" -> {
                            msisdn = r.getPhoneNumber();
                            msg = "Your SIM is active.";
                        }
                        case "inactive" -> msg = "Your SIM is inactive due to no recharge.";
                        case "deactivated" -> msg = "Number permanently disconnected (TRAI).";
                        default -> msg = "Unknown status.";
                    }

                    return ResponseEntity.ok(new StatusCheckResponse(s, msg, msisdn));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ---------------- Generate Number ----------------
    @PostMapping("/generate-number")
    @Transactional
    public ResponseEntity<?> generateNumber(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> req) {

        Long userId = extractUserIdFromToken(authHeader);
        String fourDigits = req.get("fourDigits");

        if (fourDigits == null || !fourDigits.matches("\\d{4}")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fourDigits must be exactly 4 digits.");
        }

        // Find user's active SIM request
        Optional<SimRequest> opt = simRequestRepository.findByUserIdAndStatusIn(userId, List.of("Approved", "Progress"));
        if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Active SIM request not found.");

        SimRequest sr = opt.get();

        // existing number generation logic with sr.getRequestId() internally
        String curr = Optional.ofNullable(sr.getStatus()).orElse("");

        if (!"approved".equalsIgnoreCase(curr) && !"progress".equalsIgnoreCase(curr)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Number generation is only allowed for 'Approved' or 'Progress' statuses.");
        }

        Set<String> out = new LinkedHashSet<>();
        Random rnd = new Random();
        String[] prefixes = {"9", "8", "7"};
        int attempts = 0;

        while (out.size() < 5 && attempts < 200) {
            String prefix = prefixes[rnd.nextInt(prefixes.length)];
            StringBuilder five = new StringBuilder();
            for (int i = 0; i < 5; i++) five.append(rnd.nextInt(10));
            String candidate = prefix + five + req.get("fourDigits");
            if (!allocatedNumberRepository.existsByPhoneNumber(candidate)) {
                out.add(candidate);
            }
            attempts++;
        }

        if (out.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());

        if ("approved".equalsIgnoreCase(curr)) {
            sr.setStatus("Progress");
            simRequestRepository.save(sr);
        }

        return ResponseEntity.ok(new ArrayList<>(out));
    }

    // ---------------- Select Number ----------------
    @PostMapping("/select-number")
    @Transactional
    public ResponseEntity<String> selectNumber(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> req) {  // Accept generic JSON Map with selectedNumber key

        Long userId = extractUserIdFromToken(authHeader);

        String selectedNumber = req.get("selectedNumber");
        if (selectedNumber == null || selectedNumber.isEmpty()) {
            return ResponseEntity.badRequest().body("selectedNumber is required.");
        }

        Optional<SimRequest> opt = simRequestRepository.findByUserIdAndStatus(userId, "Progress");
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No SIM request in progress.");
        }

        if (allocatedNumberRepository.existsByPhoneNumber(selectedNumber)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Number already taken. Generate a new list.");
        }

        AllocatedNumber alloc = new AllocatedNumber();
        alloc.setPhoneNumber(selectedNumber);
        alloc.setRequestId(opt.get().getRequestId());
        allocatedNumberRepository.save(alloc);

        SimRequest sr = opt.get();
        sr.setPhoneNumber(selectedNumber);
        sr.setStatus("Provisioning");
        sr.setProvisionedAt(Instant.now());
        simRequestRepository.save(sr);

        String email = getEmailFromToken(authHeader);

        notifyUserWithDetails(userId, email,
                "Number Selected",
                "You have selected the number " + selectedNumber + ". Your SIM will be activated within 24 hours.");

        return ResponseEntity.ok("Number selected! Activation within 24 hours.");
    }
    
    @GetMapping("/requests/status")
    public ResponseEntity<String> getSimRequestStatus(@RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromToken(authHeader);
        Optional<SimRequest> simRequest = simRequestRepository.findByUserId(userId);
        if (simRequest.isPresent()) {
            return ResponseEntity.ok(simRequest.get().getStatus().toLowerCase());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("none");
        }
    }


}
