package com.example.documentverification;

import com.example.documentverification.DocumentDetails;
import com.example.documentverification.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api/docs")
public class DocumentController {

    @Autowired
    private DocumentService service;

    @PostMapping("/upload")
    public ResponseEntity<DocumentDetails> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId,
            @RequestParam("cardType") String cardType
    ) throws Exception {
        File temp = File.createTempFile("upload-", file.getOriginalFilename());
        file.transferTo(temp);

        DocumentDetails doc = new DocumentDetails();
        doc.setUserId(userId);
        doc.setCardType(cardType);
        
        
        DocumentDetails saved = service.processDocument(doc, temp);


        temp.delete();
        return ResponseEntity.ok(saved);
    }
}
