// src/main/java/com/example/backend/service/CategoryService.java
package com.example.backend.service;

import com.example.backend.model.Category;
import com.example.backend.model.User;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository; // Para obtener el usuario por email

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    // Método para crear categorías por defecto si no existen
    public void createDefaultCategories() {
        String[] defaultCategoryNames = {"FAMILY", "FRIENDS", "JOB", "KNOWS", "OTHER"};
        for (String name : defaultCategoryNames) {
            // Si no encuentra una categoría con ese nombre y sin usuario asignado, la crea
            if (categoryRepository.findByNameAndUserIsNull(name).isEmpty()) {
                categoryRepository.save(new Category(name, null)); // user es null para categorías por defecto
            }
        }
    }

    // Obtiene todas las categorías para un usuario dado su email (por defecto + personalizadas)
    public List<Category> getAllCategoriesForUser(String userEmail) {
        List<Category> categories = new ArrayList<>();
        // Añade categorías por defecto
        categories.addAll(categoryRepository.findByUserIsNull());

        // Añade categorías específicas del usuario
        userRepository.findByEmail(userEmail).ifPresent(user -> {
            categories.addAll(categoryRepository.findByUserId(user.getId()));
        });
        return categories;
    }

    // Método principal para encontrar o crear una categoría
    public Optional<Category> findOrCreateCategory(String categoryName, String userEmail) {
        // Intenta encontrar la categoría como personalizada para el usuario actual
        Optional<Category> category = Optional.empty();
        if (userEmail != null && !userEmail.isEmpty()) {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado para el email: " + userEmail));
            category = categoryRepository.findByNameAndUserId(categoryName, user.getId());
        }

        // Si no se encuentra como personalizada, intenta encontrarla como categoría por defecto
        if (category.isEmpty()) {
            category = categoryRepository.findByNameAndUserIsNull(categoryName);
        }

        // Si aún no se encuentra y se proporcionó un email de usuario, crea una nueva categoría personalizada
        if (category.isEmpty() && userEmail != null && !userEmail.isEmpty()) {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado para el email: " + userEmail));
            category = Optional.of(categoryRepository.save(new Category(categoryName, user)));
        }
        // Si sigue sin encontrarse y no hay email de usuario, significa que es una categoría por defecto inválida.
        return category;
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}