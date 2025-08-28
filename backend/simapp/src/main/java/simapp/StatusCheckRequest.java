package simapp;

public class StatusCheckRequest {
    private String email;
    private String requestId;

    public StatusCheckRequest() {}

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
}
