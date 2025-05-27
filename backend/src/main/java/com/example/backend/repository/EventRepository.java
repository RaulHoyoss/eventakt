package com.example.backend.repository;

import com.example.backend.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    // Por ejemplo, listar los eventos de un usuario
    List<Event> findByUserId(Long userId);
}
