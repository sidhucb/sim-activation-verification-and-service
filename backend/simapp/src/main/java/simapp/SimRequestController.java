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
@CrossOrigin(origins = "http://localhost:3000")
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
    private void notifyUser(String email, String message) {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", email);
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
            @RequestBody GenerateNumberRequest req) {

        Long userId = extractUserIdFromToken(authHeader);

        if (req.getFourDigits() == null || !req.getFourDigits().matches("\\d{4}")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fourDigits must be exactly 4 digits.");
        }

        var opt = simRequestRepository.findByUserIdAndRequestId(userId, req.getRequestId());
        if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request not found.");

        SimRequest sr = opt.get();
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
            String candidate = prefix + five + req.getFourDigits();
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
            @RequestBody SelectNumberRequest req) {

        Long userId = extractUserIdFromToken(authHeader);

        var opt = simRequestRepository.findByUserIdAndRequestId(userId, req.getRequestId());
        if (opt.isEmpty() || !"progress".equalsIgnoreCase(opt.get().getStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not in a valid state for number selection.");
        }

        if (allocatedNumberRepository.existsByPhoneNumber(req.getSelectedNumber())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Number already taken. Generate a new list.");
        }

        AllocatedNumber alloc = new AllocatedNumber();
        alloc.setPhoneNumber(req.getSelectedNumber());
        alloc.setRequestId(req.getRequestId());
        allocatedNumberRepository.save(alloc);

        SimRequest sr = opt.get();
        sr.setPhoneNumber(req.getSelectedNumber());
        sr.setStatus("Provisioning");
        sr.setProvisionedAt(Instant.now());
        simRequestRepository.save(sr);

        String email = getEmailFromToken(authHeader);
        notifyUser(email, "You have selected a number. SIM will be activated within 24 hours.");

        return ResponseEntity.ok("Number selected! Activation within 24 hours.");
    }
}
