package simapp;

public class SelectNumberRequest {
    private String requestId;
    private String email;
    private String selectedNumber;

    public SelectNumberRequest() {}

    public String getRequestId() {
        return requestId;
    }
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getSelectedNumber() {
        return selectedNumber;
    }
    public void setSelectedNumber(String selectedNumber) {
        this.selectedNumber = selectedNumber;
    }
}
