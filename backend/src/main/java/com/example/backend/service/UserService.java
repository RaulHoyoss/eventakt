package com.example.backend.service;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String name) {
        return userRepository.findByName(name);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAllByEmails(List<String> emails) {
        return userRepository.findAllByEmailIn(emails);
    }


    public User registerUser(String name, String email, String password, String phone, MultipartFile profileImage) {
        // Verificar si ya existe el usuario, encriptar contraseña, guardar imagen, etc.
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);

        // Si quieres guardar la imagen, aquí puedes hacerlo y guardar el path
        if (profileImage != null && !profileImage.isEmpty()) {
            String imagePath = fileStorageService.save(profileImage); // implementa este servicio si lo necesitas
            user.setProfileImagePath(imagePath);
        }

        return userRepository.save(user);
    }

}
