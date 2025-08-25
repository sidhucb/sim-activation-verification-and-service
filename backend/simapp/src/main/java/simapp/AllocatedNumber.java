package simapp;


import lombok.Data;
import jakarta.persistence.*;
import java.time.Instant;

@Data
@Entity
@Table(name = "allocated_numbers")
public class AllocatedNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    // Link the allocated number to the specific SIM request
    @Column(name = "request_id", unique = true, nullable = false)
    private String requestId;

    private Instant allocatedAt = Instant.now();
}