package com.example.backend.service.impl;

import com.example.backend.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class FileStorageServiceImpl extends FileStorageService {

    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public FileStorageServiceImpl() throws IOException {
        super();
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo crear el directorio para subir archivos", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            // Copia el archivo al directorio destino
            Path targetLocation = fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Devuelve la ruta relativa (podrías devolver solo el nombre si lo prefieres)
            return "/uploads/" + fileName;
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo almacenar el archivo " + fileName, ex);
        }
    }
}