package simapp;

import lombok.Data;

@Data

public class StatusCheckRequest {
    private String email;
    private String requestId;
}