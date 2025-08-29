package com.example.documentverification.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHelper {

    private static final String UPLOAD_DIR = "uploads/";

    public static String saveUploadedFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;
        Path path = Paths.get(UPLOAD_DIR + file.getOriginalFilename());
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        return path.toString();
    }
}
