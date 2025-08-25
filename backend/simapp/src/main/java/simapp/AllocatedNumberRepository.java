package simapp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AllocatedNumberRepository extends JpaRepository<AllocatedNumber, Long> {
    // Method to quickly check if a number already exists
    boolean existsByPhoneNumber(String phoneNumber);

    // Method to find the allocation details for a given request
    Optional<AllocatedNumber> findByRequestId(String requestId);
}