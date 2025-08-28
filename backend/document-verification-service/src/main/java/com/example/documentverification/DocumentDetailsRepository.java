package com.example.documentverification;

import com.example.documentverification.DocumentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocumentDetailsRepository extends JpaRepository<DocumentDetails, Long> {
    List<DocumentDetails> findByStatus(String status);
    List<DocumentDetails> findByUserId(String userId);
}
