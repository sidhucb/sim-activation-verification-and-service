package com.example.documentverification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface DocumentRepository extends JpaRepository<DocumentDetails, Long> {

    // Already existing method
    List<DocumentDetails> findByStatus(String status);

    // 👉 Add this method with @Query
    @Query(value = "SELECT id, extractedname AS name, extractedage AS age, extractedaddress AS address, " +
                   "CASE WHEN extractedage < 18 " +
                   "THEN 'Not eligible for SIM (below 18 as per Indian law)' " +
                   "ELSE 'Eligible for SIM' END AS eligibility_status " +
                   "FROM document_details",
           nativeQuery = true)
    List<Object[]> findEligibilityStatus();
}
