package com.example.backend.repository;

import com.example.backend.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    // Por ejemplo, listar los contactos de un usuario
    List<Contact> findByUserId(Long userId);
}
