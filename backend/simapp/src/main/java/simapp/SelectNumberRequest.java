package simapp;

import lombok.Data;

@Data
public class SelectNumberRequest {
    private String requestId;
    private String email;
    private String selectedNumber;
}