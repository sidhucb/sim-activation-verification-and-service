package com.cardactivation;

import org.springframework.context.annotation.Primary;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
@Primary
public interface CardActivationService {
    CardActivationEntity uploadAndSaveFile(MultipartFile file, String userName) throws IOException;
    String findByExtractedText(Long id);
    String verifyKyc(Long id);
    CardActivationEntity findByUserName(String userName);
}
