package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;

@Service
public class SimulatedPqcCryptoService {

    @Autowired
    private SimulatedBlockchainService blockchainService; // Inject the blockchain service

    private KeyPair userKeyPair;

    @PostConstruct
    public void init() throws NoSuchAlgorithmException {
        // This simulates the iSIM/device generating its keys locally.
        // The private key would NEVER leave the device.
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        userKeyPair = generator.generateKeyPair();

        // The public key is then registered on the blockchain.
        String userPublicKey = Base64.getEncoder().encodeToString(userKeyPair.getPublic().getEncoded());
        blockchainService.registerPublicKey(userPublicKey);

        System.out.println("\n--- SIMULATED Crypto Service Initialized ---");
        System.out.println("Algorithm: RSA (simulating a PQC algorithm)");
        System.out.println("User Public Key generated and registered on the blockchain: " + userPublicKey);
        System.out.println("--- End Crypto Service Init ---\n");
    }

    /**
     * Verifies a signature against a challenge using the provided public key,
     * after ensuring the public key is registered on the blockchain.
     */
    public boolean verifySignature(String challenge, String signatureB64, String publicKeyString) {
        try {
            // 1. Query the blockchain to ensure the public key is officially registered.
            Optional<String> registeredKey = blockchainService.findPublicKey(publicKeyString);
            if (registeredKey.isEmpty()) {
                System.err.println("CRYPTO_SERVICE: Verification FAILED. Public key not found on the blockchain.");
                return false;
            }
            System.out.println("CRYPTO_SERVICE: Public key successfully verified against blockchain record.");

            // 2. Proceed with signature verification
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            Signature publicSignature = Signature.getInstance("SHA256withRSA");
            publicSignature.initVerify(publicKey);
            publicSignature.update(challenge.getBytes());

            byte[] signatureBytes = Base64.getDecoder().decode(signatureB64);
            return publicSignature.verify(signatureBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * SIMULATES the client-side signing process.
     * In a real app, this would be executed on the user's device.
     */
    public String signChallengeClientSide(String challenge) throws Exception {
        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initSign(userKeyPair.getPrivate());
        signer.update(challenge.getBytes());
        byte[] signature = signer.sign();
        return Base64.getEncoder().encodeToString(signature);
    }

    /**
     * Helper method to get the public key of the simulated user.
     * In a real-world scenario, the client would send its public key to the server.
     * @return The Base64 encoded public key.
     */
    public String getPublicKeyAsString() {
        return Base64.getEncoder().encodeToString(userKeyPair.getPublic().getEncoded());
    }
}