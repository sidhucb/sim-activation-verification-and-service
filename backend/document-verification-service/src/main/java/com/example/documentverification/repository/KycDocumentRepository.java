package com.example.documentverification.repository;

import com.example.documentverification.model.KycDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface KycDocumentRepository extends JpaRepository<KycDocument, Long> {
    Optional<KycDocument> findByUserId(String userId);
}
