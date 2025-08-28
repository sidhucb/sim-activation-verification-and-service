package simapp;

public class GenerateNumberRequest {
    private String email;
    private String requestId;
    private String fourDigits;

    public GenerateNumberRequest() {}

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getRequestId() {
        return requestId;
    }
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    public String getFourDigits() {
        return fourDigits;
    }
    public void setFourDigits(String fourDigits) {
        this.fourDigits = fourDigits;
    }
}
