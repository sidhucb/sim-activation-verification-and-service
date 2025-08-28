package simapp;

public class StatusCheckResponse {
    private String status;
    private String message;
    private String phoneNumber;

    public StatusCheckResponse() {}

    public StatusCheckResponse(String status, String message, String phoneNumber) {
        this.status = status;
        this.message = message;
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
