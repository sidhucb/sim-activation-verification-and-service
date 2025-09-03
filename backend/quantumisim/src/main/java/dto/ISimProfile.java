package dto;

import java.util.UUID;

public class ISimProfile {
    private String profileId;
    private String provider;
    private String iccid;
    private String activationCode;
    private String status;

    public ISimProfile() {
        this.profileId = "prof-" + UUID.randomUUID().toString();
        this.provider = "Gemini StandardNet";
        this.iccid = "89014103211" + (long) (Math.random() * 100000000L);
        this.activationCode = "LPA:1$standardnet.com$" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        this.status = "PROVISIONED";
    }

    // Getters and Setters
    public String getProfileId() { return profileId; }
    public void setProfileId(String profileId) { this.profileId = profileId; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getIccid() { return iccid; }
    public void setIccid(String iccid) { this.iccid = iccid; }
    public String getActivationCode() { return activationCode; }
    public void setActivationCode(String activationCode) { this.activationCode = activationCode; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}