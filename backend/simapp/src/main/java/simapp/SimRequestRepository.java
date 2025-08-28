package simapp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Import List
import java.util.Optional;

@Repository
public interface SimRequestRepository extends JpaRepository<SimRequest, Long> {

    Optional<SimRequest> findByUserIdAndRequestId(Long userId, String requestId);
    
    List<SimRequest> findByStatus(String status);
}
