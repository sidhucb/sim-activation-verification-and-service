package simapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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

    // This /status endpoint is correct and needs no changes.
    // The logic to check for activation has been correctly moved to the scheduler.
    @PostMapping("/status")
    @Transactional
    public ResponseEntity<StatusCheckResponse> checkRequestStatus(@RequestBody StatusCheckRequest statusCheckRequest) {
        Optional<SimRequest> requestOptional = simRequestRepository.findByEmailAndRequestId(
                statusCheckRequest.getEmail(),
                statusCheckRequest.getRequestId()
        );

        if (requestOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        SimRequest request = requestOptional.get();
        String status = request.getStatus();
        String message;
        String phoneNumber = null;

        switch (status.toLowerCase()) {
            case "pending":
                message = "Your application has been submitted and is pending review.";
                break;
            case "approved":
                message = "Congratulations, your KYC has been approved! You can now proceed to generate and select your mobile number.";
                break;
            case "progress":
                message = "You are in the process of selecting a mobile number. Please choose one from the generated list.";
                break;
            case "provisioning":
                message = "Your selected number is being provisioned. The SIM will be activated within 24 hours.";
                break;
            case "active":
                phoneNumber = request.getPhoneNumber();
                message = "Your SIM has been activated and is ready to use.";
                break;
            case "inactive":
                message = "Your SIM is inactive because it has not been recharged for an extended period.";
                break;
            case "deactivated":
                message = "This number has been permanently disconnected in accordance with TRAI regulations.";
                break;
            default:
                message = "Unknown status.";
                break;
        }

        return ResponseEntity.ok(new StatusCheckResponse(status, message, phoneNumber));
    }

    @PostMapping("/generate-number")
    @Transactional
    public ResponseEntity<?> generateNumber(@RequestBody GenerateNumberRequest request) {
        Optional<SimRequest> requestOptional = simRequestRepository.findByEmailAndRequestId(request.getEmail(), request.getRequestId());

        if (requestOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request not found.");
        }

        SimRequest simRequest = requestOptional.get();
        String currentStatus = simRequest.getStatus();

        // --- FIX ---
        // Allow number generation for users in 'Approved' OR 'Progress' state.
        if (!"approved".equalsIgnoreCase(currentStatus) && !"progress".equalsIgnoreCase(currentStatus)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Number generation is only allowed for 'Approved' or 'Progress' statuses.");
        }

        // --- Number generation logic (remains the same) ---
        Set<String> generatedNumbers = new HashSet<>();
        Random random = new Random();
        String[] prefixes = {"9", "8", "7"};
        int attempts = 0;
        while (generatedNumbers.size() < 5 && attempts < 100) {
            String prefix = prefixes[random.nextInt(prefixes.length)];
            StringBuilder randomNumberPart = new StringBuilder();
            for (int j = 0; j < 6; j++) {
                randomNumberPart.append(random.nextInt(10));
            }
            randomNumberPart.insert(random.nextInt(randomNumberPart.length() + 1), request.getFourDigits());
            String finalNumber = prefix + randomNumberPart.substring(0, 9);

            if (!allocatedNumberRepository.existsByPhoneNumber(finalNumber)) {
                generatedNumbers.add(finalNumber);
            }
            attempts++;
        }

        if (generatedNumbers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }

        // --- FIX ---
        // Only change the status to 'Progress' if it's the first time ('Approved').
        if ("approved".equalsIgnoreCase(currentStatus)) {
            simRequest.setStatus("Progress");
            simRequestRepository.save(simRequest);
        }

        return ResponseEntity.ok(new ArrayList<>(generatedNumbers));
    }

    @PostMapping("/select-number")
    @Transactional
    public ResponseEntity<String> selectNumber(@RequestBody SelectNumberRequest selectRequest) {
        Optional<SimRequest> simRequestOptional = simRequestRepository
                .findByEmailAndRequestId(selectRequest.getEmail(), selectRequest.getRequestId());

        if (simRequestOptional.isEmpty() || !"Progress".equalsIgnoreCase(simRequestOptional.get().getStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This request is not in a valid state for number selection.");
        }

        if (allocatedNumberRepository.existsByPhoneNumber(selectRequest.getSelectedNumber())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Sorry, this number was just taken. Please generate a new list.");
        }

        AllocatedNumber newAllocation = new AllocatedNumber();
        newAllocation.setPhoneNumber(selectRequest.getSelectedNumber());
        newAllocation.setRequestId(selectRequest.getRequestId());
        allocatedNumberRepository.save(newAllocation);

        SimRequest simRequest = simRequestOptional.get();
        simRequest.setPhoneNumber(selectRequest.getSelectedNumber());
        simRequest.setStatus("Provisioning");
        
        // --- FIX ---
        // Set the provisionedAt timestamp so the background scheduler can activate it.
        simRequest.setProvisionedAt(Instant.now()); 
        simRequestRepository.save(simRequest);

        return ResponseEntity.ok("Number selected successfully! Your SIM will be activated within 24 hours.");
    }
}