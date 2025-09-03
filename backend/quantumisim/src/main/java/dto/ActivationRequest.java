package dto;

public class ActivationRequest {
    private String challenge;
    private String signature; // Base64 encoded signature

    // Getters and Setters
    public String getChallenge() { return challenge; }
    public void setChallenge(String challenge) { this.challenge = challenge; }
    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
}