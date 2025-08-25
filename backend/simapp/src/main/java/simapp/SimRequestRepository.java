package simapp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Import List
import java.util.Optional;

@Repository
public interface SimRequestRepository extends JpaRepository<SimRequest, Long> {

    Optional<SimRequest> findByEmailAndRequestId(String email, String requestId);

    /**
     * New method: Finds all SIM requests with a specific status.
     * This is used by the SimStatusScheduler to fetch all 'Provisioning' requests.
     */
    List<SimRequest> findByStatus(String status);
}