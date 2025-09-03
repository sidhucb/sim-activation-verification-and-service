package dto;

public class TransactionRequest {
    private double amount;
    private String merchant;
    private String transactionLocation; // e.g., "New York, NY" or a GPS coordinate

    // Getters and Setters
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getMerchant() { return merchant; }
    public void setMerchant(String merchant) { this.merchant = merchant; }
    public String getTransactionLocation() { return transactionLocation; }
    public void setTransactionLocation(String transactionLocation) { this.transactionLocation = transactionLocation; }
}