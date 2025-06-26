// src/main/java/com/example/backend/repository/CategoryRepository.java
package com.example.backend.repository;

import com.example.backend.model.Category;
import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Busca categorías por defecto (user_id es null)
    List<Category> findByUserIsNull();

    // **NUEVO/CORREGIDO:** Busca categorías por defecto Y las ordena por nombre ascendente
    List<Category> findByUserIsNullOrderByNameAsc();

    // **NUEVO/CORREGIDO:** Busca categorías de un usuario específico Y las ordena por nombre ascendente
    List<Category> findByUserOrderByNameAsc(User user);

    // Busca categorías específicas de un usuario por su ID
    List<Category> findByUserId(Long userId);

    // Busca una categoría por nombre y ID de usuario (para categorías personalizadas)
    Optional<Category> findByNameAndUserId(String name, Long userId);

    // Busca una categoría por nombre y si es una categoría por defecto
    Optional<Category> findByNameAndUserIsNull(String name);
}