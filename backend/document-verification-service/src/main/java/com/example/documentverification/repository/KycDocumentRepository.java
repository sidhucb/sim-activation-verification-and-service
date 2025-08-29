package com.example.documentverification.repository;

import com.example.documentverification.model.KycDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KycDocumentRepository extends JpaRepository<KycDocument, Long> {
    // Optional: findByUserId
    KycDocument findByUserId(String userId);
}
