package com.example.documentverification.manual;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.documentverification.util.MaskingUtil;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Service
public class ManualDocumentService {

    @Autowired
    private UserDocumentManualRepository repository;

    public UserDocumentManual processManualDocument(UserDocumentManual doc) {
        try {
            // ---------------- Mask ID ----------------
            if (doc.getIdNumber() != null) {
                if (doc.getIdNumber().length() == 10) {
                    doc.setIdNumber(MaskingUtil.maskPan(doc.getIdNumber()));
                } else if (doc.getIdNumber().length() == 12) {
                    doc.setIdNumber(MaskingUtil.maskAadhar(doc.getIdNumber()));
                }
            }

            // ---------------- Age Eligibility ----------------
            if (doc.getDob() != null && !doc.getDob().isEmpty()) {
                LocalDate dob = LocalDate.parse(doc.getDob(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                int age = Period.between(dob, LocalDate.now()).getYears();
                if (age < 18) {
                    doc.setSimEligibilityMessage("Under 18 - cannot get SIM");
                    doc.setStatus("pending_manual");
                } else {
                    doc.setSimEligibilityMessage("Eligible for SIM");
                    doc.setStatus("processed");
                }
            } else {
                doc.setSimEligibilityMessage("DOB not provided, please verify manually");
                doc.setStatus("pending_manual");
            }

        } catch (Exception e) {
            doc.setStatus("manual_failed");
            doc.setSimEligibilityMessage("Manual processing failed. Please check data");
        }

        return repository.save(doc);
    }
}
