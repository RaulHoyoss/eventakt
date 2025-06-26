// src/main/java/com/example/backend/controller/ContactController.java
package com.example.backend.controller;

import com.example.backend.model.Contact;
import com.example.backend.model.Category; // Importar Category
import com.example.backend.service.ContactService;
import com.example.backend.service.CategoryService; // Importar CategoryService
import org.springframework.security.core.Authentication;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contacts")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class ContactController {

    private final ContactService contactService;
    private final CategoryService categoryService; // Inyectar CategoryService

    public ContactController(ContactService contactService, CategoryService categoryService) {
        this.contactService = contactService;
        this.categoryService = categoryService;
    }

    @GetMapping("/all")
    public List<Contact> getAllContacts() {
        return contactService.getAllContacts();
    }

    @GetMapping("/user/{userId}")
    public List<Contact> getContactsByUserId(@PathVariable Long userId) {
        return contactService.getContactsByUserId(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable Long id) {
        return contactService.getContactById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Contact createContact(
            @RequestPart("name") String name,
            @RequestPart("email") String email,
            @RequestPart("phone") String phone,
            @RequestPart("category") String categoryName, // Ahora se espera el nombre de la categoría
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        return contactService.saveContactWithPhoto(name, email, phone, categoryName, photo, userEmail);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<Contact> getUserContacts(Authentication authentication) {
        String email = authentication.getName();
        return contactService.getContactsByUserEmail(email);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Contact> updateContact(
            @PathVariable Long id,
            @RequestPart("name") String name,
            @RequestPart("email") String email,
            @RequestPart("phone") String phone,
            @RequestPart("category") String categoryName, // Ahora se espera el nombre de la categoría
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            Authentication authentication) {

        String userEmail = authentication.getName();
        Optional<Contact> updated = contactService.updateContact(id, name, email, phone, categoryName, photo, userEmail);
        return updated.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Nuevo endpoint para obtener todas las categorías para el usuario logueado
    @GetMapping("/categories")
    public List<Category> getUserCategories(Authentication authentication) {
        String userEmail = authentication.getName();
        return categoryService.getAllCategoriesForUser(userEmail);
    }
}