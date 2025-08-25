package simapp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusCheckResponse {
    private String status;
    private String message;
    private String phoneNumber;
}