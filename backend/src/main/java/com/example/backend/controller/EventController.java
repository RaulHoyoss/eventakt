package com.example.backend.controller;

import com.example.backend.model.Event;
import com.example.backend.model.dto.EventDTO;
import com.example.backend.model.dto.EventResponseDTO;
import com.example.backend.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventResponseDTO> getAllEvents() {
        return eventService.getAllEvents().stream()
                .map(eventService::mapToDto)
                .toList();
    }

    @GetMapping("/user/{userId}")
    public List<EventResponseDTO> getEventsByUserId(@PathVariable Long userId) {
        return eventService.getEventsByUserId(userId).stream()
                .map(eventService::mapToDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(event -> ResponseEntity.ok(eventService.mapToDto(event)))
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(@RequestBody EventDTO dto) {
        Event created = eventService.createEventFromDto(dto);
        EventResponseDTO responseDto = eventService.mapToDto(created);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        Optional<Event> existing = eventService.getEventById(id);
        if (!existing.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        event.setId(id); // asegura que sea el mismo
        return ResponseEntity.ok(eventService.saveEvent(event));
    }

}
