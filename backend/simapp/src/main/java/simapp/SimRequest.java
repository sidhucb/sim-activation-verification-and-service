package simapp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.Instant; // Make sure to import Instant

@Data
@Entity
@Table(name = "sim_status")
public class SimRequest {
    @Id
    private Long id;
    private String requestId;
    private String username;
    private String email;
    @Column(name = "simstatus")
    private String status;
    private String phoneNumber;

    /**
     * New field: Stores the exact timestamp when the status changes to 'Provisioning'.
     * This will be used by the scheduler to check if 24 hours have passed.
     */
    private Instant provisionedAt;
}