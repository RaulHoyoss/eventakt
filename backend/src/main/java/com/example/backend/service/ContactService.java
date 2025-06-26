// src/main/java/com/example/backend/service/ContactService.java
package com.example.backend.service;

import com.example.backend.model.Contact;
import com.example.backend.model.User;
import com.example.backend.model.Category; // Importar Category
import com.example.backend.repository.ContactRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService; // Inyectar CategoryService

    @Value("${upload.dir}") // Asegúrate de tener esta propiedad configurada en application.properties
    private String uploadDir;

    public ContactService(ContactRepository contactRepository, UserRepository userRepository, CategoryService categoryService) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
        this.categoryService = categoryService;
    }

    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    public List<Contact> getContactsByUserId(Long userId) {
        return contactRepository.findByUserId(userId);
    }

    public Optional<Contact> getContactById(Long id) {
        return contactRepository.findById(id);
    }

    // Nuevo método para guardar contacto, ahora recibe categoryName (String)
    public Contact saveContactWithPhoto(String name, String email, String phone, String categoryName, MultipartFile photo, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para el email: " + userEmail));

        // Encontrar o crear la categoría basada en el nombre y el usuario
        Category category = categoryService.findOrCreateCategory(categoryName, userEmail)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada o no pudo ser creada: " + categoryName));

        Contact contact = new Contact();
        contact.setName(name);
        contact.setEmail(email);
        contact.setPhone(phone);
        contact.setUser(user);
        contact.setCategory(category); // Asigna el objeto Category

        if (photo != null && !photo.isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" + photo.getOriginalFilename();
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(photo.getInputStream(), filePath);
                contact.setPhotoUrl("/uploads/" + fileName); // Asumiendo que '/uploads/' es tu mapeo de contenido estático
            } catch (IOException e) {
                throw new RuntimeException("Fallo al almacenar la foto", e);
            }
        }
        return contactRepository.save(contact);
    }

    public void deleteContact(Long id) {
        contactRepository.deleteById(id);
    }

    public List<Contact> getContactsByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para el email: " + email));
        return contactRepository.findByUserId(user.getId());
    }

    // Método para actualizar contacto, ahora recibe categoryName (String)
    public Optional<Contact> updateContact(Long id, String name, String email, String phone, String categoryName, MultipartFile photo, String userEmail) {
        return contactRepository.findById(id).map(contact -> {
            contact.setName(name);
            contact.setEmail(email);
            contact.setPhone(phone);

            // Encontrar o crear la categoría basada en el nombre y el usuario
            Category category = categoryService.findOrCreateCategory(categoryName, userEmail)
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada o no pudo ser creada: " + categoryName));
            contact.setCategory(category); // Actualiza el objeto Category

            if (photo != null && !photo.isEmpty()) {
                try {
                    String fileName = UUID.randomUUID().toString() + "_" + photo.getOriginalFilename();
                    Path uploadPath = Paths.get(uploadDir);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(photo.getInputStream(), filePath);
                    contact.setPhotoUrl("/uploads/" + fileName);
                } catch (IOException e) {
                    throw new RuntimeException("Fallo al almacenar la foto", e);
                }
            }
            return contactRepository.save(contact);
        });
    }
}