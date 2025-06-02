package com.example.backend.service;

import com.example.backend.model.Contact;
import com.example.backend.model.User;
import com.example.backend.repository.ContactRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
;


@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private static final String uploadFolder = "uploads";



    public ContactService(ContactRepository contactRepository, UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
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

    public Contact saveContact(Contact contact) {
        return contactRepository.save(contact);
    }

    public void deleteContact(Long id) {
        contactRepository.deleteById(id);
    }

    public List<Contact> getContactsByUserEmail(String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return contactRepository.findByUserId(user.getId());
    }

    public Contact saveContactForUser(Contact contact, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        contact.setUser(user);
        return contactRepository.save(contact);
    }

    public Contact saveContactWithPhoto(String name, String email, String phone, MultipartFile photo, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));

        Contact contact = new Contact();
        contact.setName(name);
        contact.setEmail(email);
        contact.setPhone(phone);
        contact.setUser(user);

        if (photo != null && !photo.isEmpty()) {
            try {
                Path uploadPath = Paths.get("uploads");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String filename = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
                Path filePath = uploadPath.resolve(filename);
                Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                String photoUrl = "http://localhost:8081/uploads/" + filename;
                contact.setPhotoUrl(photoUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save photo", e);
            }
        }

        return contactRepository.save(contact);
    }
    public Optional<Contact> updateContact(Long id, String name, String email, String phone, MultipartFile photo, String userEmail) {
        Optional<Contact> optionalContact = contactRepository.findById(id);
        if (optionalContact.isEmpty()) return Optional.empty();

        Contact contact = optionalContact.get();
        if (!contact.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        contact.setName(name);
        contact.setEmail(email);
        contact.setPhone(phone);

        if (photo != null && !photo.isEmpty()) {
            try {
                Path uploadPath = Paths.get("uploads");
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
                String filename = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
                Path filePath = uploadPath.resolve(filename);
                Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                contact.setPhotoUrl("http://localhost:8081/uploads/" + filename);
            } catch (IOException e) {
                throw new RuntimeException("Failed to update photo", e);
            }
        }

        return Optional.of(contactRepository.save(contact));
    }

}
