package com.example.backend.repository;

import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Puedes agregar métodos personalizados aquí si quieres
    Optional<User> findByName(String name);

    Optional<User> findByEmail(String email);

    List<User> findAllByEmailIn(List<String> emails);

}
