package com.example.documentverification.manual;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDocumentManualRepository extends JpaRepository<UserDocumentManual, Long> {
    List<UserDocumentManual> findByStatus(String status);
    List<UserDocumentManual> findByUserId(Long userId);
}
