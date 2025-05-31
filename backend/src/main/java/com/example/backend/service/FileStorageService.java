package com.example.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.UUID;

@Service
public abstract class FileStorageService {

    private final Path uploadDir = Paths.get("uploads");

    public FileStorageService() throws IOException {
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }

    public String save(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String uniqueFilename = UUID.randomUUID().toString() + extension;
            Path destination = uploadDir.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            return destination.toString(); // o solo el filename si prefieres guardarlo relativo
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la imagen de perfil", e);
        }
    }

    public abstract String storeFile(MultipartFile file);
}

