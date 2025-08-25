package CheckSim;

import jakarta.persistence.*;

@Entity
@Table(name = "sim_status")
public class simstatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;

    @Column(name = "simstatus") // The column name in your database
    private String simStatus;

    @Column(name = "request_id") // Add this field
    private String requestId;

    // --- GETTERS AND SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSimStatus() {
        return simStatus;
    }

    public void setSimStatus(String simStatus) {
        this.simStatus = simStatus;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}