package simapp;

import lombok.Data;

@Data
public class GenerateNumberRequest {
    private String email;
    private String requestId;
    private String fourDigits;
}