package com.example.demo;

import dto.ActivationRequest;
import dto.TransactionRequest;
import dto.ISimProfile;
import com.fazecast.jSerialComm.SerialPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

@RestController
@RequestMapping("/api/activation")
@CrossOrigin(origins = "http://localhost:3000")
public class ActivationController {
    // private static final String USER_CURRENT_LOCATION = "New York, NY"; // <-- DELETED
    private static final String ARDUINO_PORT_NAME = "COM6"; // CHANGE THIS TO YOUR PORT
    private static final int BAUD_RATE = 9600;
    		@Autowired
    	    private DlAgentService dlAgentService;
    @Autowired
    private SimulatedPqcCryptoService cryptoService;
    @Autowired
    private MockGpsService gpsService; // <-- INJECTED

    // --- UPDATED TRANSACTION ENDPOINT ---
    @PostMapping("/authorize-transaction")
    public ResponseEntity<Map<String, String>> authorizeTransaction(@RequestBody TransactionRequest txRequest) {
        System.out.println("\n--- New Transaction Request Received ---");
        System.out.println("Merchant: " + txRequest.getMerchant() + ", Amount: " + txRequest.getAmount());

        // 1. Get the user's "real-time" location from the mock GPS service
        String currentUserLocation = gpsService.getCurrentUserLocation();
        String transactionLocation = txRequest.getTransactionLocation();

        System.out.println("Geofence Check: User is at '" + currentUserLocation + "', Transaction is at '" + transactionLocation + "'");

        // 2. Perform the dynamic geofencing check
        if (!currentUserLocation.equals(transactionLocation)) {
            System.err.println("GEFENCE ALERT: Transaction location does not match user's physical location. Denying request.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("status", "DENIED", "message", "Transaction Denied: Geofencing check failed."));
        }
        System.out.println("Geofence Check Passed.");

        // --- Hardware check logic remains the same ---
        try {
            String txChallenge = "tx-authorize-" + UUID.randomUUID().toString();
            System.out.println("Sending hardware challenge for transaction: " + txChallenge);

            // Call the FLEXIBLE method, telling it to look for "transaction_authorized("
            String arduinoResponse = communicateWithArduino(txChallenge, "transaction_authorized(");

            String expectedResponse = "transaction_authorized(" + txChallenge + ")";
            if (arduinoResponse.trim().equals(expectedResponse)) {
                 System.out.println("Hardware Proof-of-Presence SUCCESSFUL!");
                 return ResponseEntity.ok(Map.of("status", "APPROVED", "message", "Transaction of " + txRequest.getAmount() + " to " + txRequest.getMerchant() + " is successful."));
            } else {
                System.err.println("Hardware Proof-of-Presence FAILED!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Hardware confirmation failed. Transaction denied."));
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error communicating with hardware: " + e.getMessage()));
        }
    }

    // --- ORIGINAL ACTIVATION FLOW ENDPOINTS ---
    @PostMapping("/verify-document")
    public ResponseEntity<Map<String, String>> verifyDocument(@RequestParam("idDocument") MultipartFile file) {
        System.out.println("--- New Document Verification Request Received ---");
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Document file is empty."));
        }

        try {
            // Call the new DL Agent Service
            Map<String, Object> dlAnalysisResult = dlAgentService.analyzeIdDocument(file);

            boolean isValid = (boolean) dlAnalysisResult.getOrDefault("isValid", false);
            if (!isValid) {
                System.err.println("DL Agent: Document analysis FAILED: " + dlAnalysisResult.get("reason"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Document analysis failed: " + dlAnalysisResult.get("reason")));
            }

            System.out.println("DL Agent: Document analysis PASSED. Extracted Name: " + dlAnalysisResult.get("extracted_name"));
            String challenge = "isim-activation-challenge-" + UUID.randomUUID().toString();
            System.out.println("Generated PQC Challenge for client: " + challenge);

            Map<String, String> response = new HashMap<>();
            response.put("challenge", challenge);
            response.put("extractedName", (String) dlAnalysisResult.get("extracted_name")); // Pass extracted name for face verification
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error processing document: " + e.getMessage()));
        }
    }

    // --- NEW ENDPOINT FOR FACIAL VERIFICATION (after document analysis) ---
    @PostMapping("/verify-face")
    public ResponseEntity<Map<String, String>> verifyFace(@RequestParam("selfieImage") MultipartFile selfieFile,
                                                        @RequestParam("expectedName") String expectedName) {
        System.out.println("--- New Facial Verification Request Received ---");
        if (selfieFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Selfie image file is empty."));
        }

        try {
            // In a real app, you'd store the ID photo extracted from /verify-document or pass it.
            // For simulation, we'll just mock the ID photo content.
            String idPhotoBase64 = Base64.getEncoder().encodeToString("mock_id_photo_content".getBytes()); // Placeholder

            String selfieBase64 = Base64.getEncoder().encodeToString(selfieFile.getBytes());
            Map<String, Object> faceResult = dlAgentService.verifyFace(selfieBase64, idPhotoBase64, expectedName);

            boolean isMatch = (boolean) faceResult.getOrDefault("isMatch", false);
            if (!isMatch) {
                System.err.println("DL Agent: Facial verification FAILED: " + faceResult.get("reason"));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Facial verification failed: " + faceResult.get("reason")));
            }
            System.out.println("DL Agent: Facial verification PASSED for " + expectedName + " with confidence: " + faceResult.get("confidence"));
            return ResponseEntity.ok(Map.of("message", "Facial verification successful."));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error processing selfie: " + e.getMessage()));
        }
    }


    @GetMapping("/get-signature-for-challenge")
    public ResponseEntity<Map<String, String>> getSignatureForChallenge(@RequestParam String challenge) throws Exception {
        String signature = cryptoService.signChallengeClientSide(challenge);
        return ResponseEntity.ok(Map.of("signature", signature));
    }

    /**
     * New helper endpoint for the client to get the public key to send back.
     * In a real app, the client would already know its own public key.
     */
    @GetMapping("/get-public-key")
    public ResponseEntity<Map<String, String>> getPublicKey() {
        return ResponseEntity.ok(Map.of("publicKey", cryptoService.getPublicKeyAsString()));
    }


    // --- INTEGRATED AND UPDATED METHOD ---
    @PostMapping("/verify-hardware-and-activate")
    public ResponseEntity<?> verifyHardwareAndActivate(@RequestBody Map<String, String> payload) {
        String challenge = payload.get("challenge");
        String signature = payload.get("signature");
        String publicKey = payload.get("publicKey"); // Receive public key from client

        if (challenge == null || signature == null || publicKey == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing challenge, signature, or public key."));
        }

        // The cryptoService now handles verification against the blockchain
        boolean isSignatureValid = cryptoService.verifySignature(challenge, signature, publicKey);

        if (!isSignatureValid) {
            System.err.println("CONTROLLER: PQC Signature Verification FAILED.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "PQC signature verification failed."));
        }
        System.out.println("Simulated PQC Signature Verification SUCCESSFUL!");

        // Hardware verification logic remains the same
        try {
            // Call the FLEXIBLE method, telling it to look for "acknowledged("
            String arduinoResponse = communicateWithArduino(challenge, "acknowledged(");

            String expectedResponse = "acknowledged(" + challenge + ")";
            if (!arduinoResponse.trim().equals(expectedResponse)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Hardware verification failed.");
            }
            System.out.println("Hardware proof-of-presence SUCCESSFUL!");
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error communicating with hardware: " + e.getMessage());
        }

        System.out.println("All verifications passed. Provisioning iSIM profile...");
        ISimProfile profile = new ISimProfile();
        return ResponseEntity.ok(profile);
    }

    // --- THE NEW, FLEXIBLE COMMUNICATION METHOD ---
    private String communicateWithArduino(String message, String expectedResponsePrefix) throws IOException, InterruptedException {
        SerialPort sp = SerialPort.getCommPort(ARDUINO_PORT_NAME);
        // Use a short, semi-blocking read timeout. This is more reliable.
        sp.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);

        if (!sp.openPort()) {
            throw new IOException("Could not open Arduino port: " + ARDUINO_PORT_NAME);
        }

        // It's crucial to give the Arduino time to reset after the port opens.
        Thread.sleep(2000);

        // Clear any leftover data from the serial buffer from the reset
        while (sp.bytesAvailable() > 0) {
            byte[] buffer = new byte[sp.bytesAvailable()];
            sp.readBytes(buffer, buffer.length);
        }

        System.out.println("Port opened and cleared. Sending challenge to Arduino: [" + message + "]");
        try (PrintWriter writer = new PrintWriter(sp.getOutputStream())) {
            writer.println(message);
            writer.flush();
        }

        System.out.println("Challenge sent. Now listening for response...");

        StringBuilder lineBuilder = new StringBuilder();
        String responseLine = "";
        long startTime = System.currentTimeMillis();
        long timeout = 15000; // 15 seconds

        while (System.currentTimeMillis() - startTime < timeout) {
            if (sp.bytesAvailable() > 0) {
                byte[] readBuffer = new byte[sp.bytesAvailable()];
                int numRead = sp.readBytes(readBuffer, readBuffer.length);

                for (int i = 0; i < numRead; i++) {
                    char c = (char)readBuffer[i];
                    if (c == '\n') {
                        String currentLine = lineBuilder.toString().trim();
                        System.out.println("DEBUG: Received line from Arduino: [" + currentLine + "]");
                        if (currentLine.startsWith(expectedResponsePrefix)) {
                            System.out.println("DEBUG: Found the expected response line!");
                            responseLine = currentLine;
                            break; // Exit the inner for-loop
                        }
                        lineBuilder.setLength(0); // Clear the builder for the next line
                    } else {
                        lineBuilder.append(c);
                    }
                }
            }
            if (!responseLine.isEmpty()) {
                break; // Exit the outer while-loop
            }
            Thread.sleep(50); // Small pause to prevent busy-waiting
        }

        sp.closePort();
        System.out.println("Port closed.");

        if (responseLine.isEmpty()) {
            throw new IOException("Did not receive the expected response '" + expectedResponsePrefix + "...' from the Arduino in time.");
        }

        return responseLine;
    }
}