package com.example.backend.controller;

import com.example.backend.model.Category;
import com.example.backend.model.Contact;
import com.example.backend.model.Event;
import com.example.backend.model.User;
import com.example.backend.model.dto.EventDTO;
import com.example.backend.model.dto.EventRequestDTO;
import com.example.backend.model.dto.EventResponseDTO;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.service.ContactService;
import com.example.backend.service.EventService;
import com.example.backend.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class EventController {

    private final EventService eventService;
    private final UserService userService;
    private final ContactService contactService;
    private final CategoryRepository categoryRepository;


    public EventController(EventService eventService, UserService userService, ContactService contactService, CategoryRepository categoryRepository) {
        this.eventService = eventService;
        this.userService = userService;
        this.contactService = contactService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<EventResponseDTO> getAllEvents() {
        return eventService.getAllEvents().stream()
                .map(eventService::mapToDto)
                .toList();
    }

    @GetMapping("/my-events")
    public ResponseEntity<?> getMyEvents(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("No autorizado");
        }

        String email = authentication.getName(); // ✅ Este método es correcto si importaste bien

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        List<EventResponseDTO> events = eventService.getEventsByUserId(user.getId())
                .stream()
                .map(eventService::mapToDto)
                .toList();

        return ResponseEntity.ok(events);
    }

    @GetMapping("/categories") // La ruta será /api/events/categories
    public ResponseEntity<List<Category>> getCategoriesForEventForm(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build(); // No autorizado si no hay principal
        }

        String email = principal.getName();
        User currentUser = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Obtener categorías por defecto (user_id es null)
        List<Category> defaultCategories = categoryRepository.findByUserIsNullOrderByNameAsc();

        // Obtener categorías personalizadas del usuario logueado
        List<Category> userCategories = categoryRepository.findByUserOrderByNameAsc(currentUser);

        // Combinar ambas listas
        List<Category> allAvailableCategories = new ArrayList<>(defaultCategories);
        allAvailableCategories.addAll(userCategories);

        List<Category> cleanCategories = allAvailableCategories.stream()
                .map(cat -> {
                    Category newCategory = new Category(); // Usar constructor sin argumentos
                    newCategory.setId(cat.getId());
                    newCategory.setName(cat.getName());
                    newCategory.setUser(null); // Asegurarse de que el user es null
                    return newCategory;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(cleanCategories);
    }




    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(event -> ResponseEntity.ok(eventService.mapToDto(event)))
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(
            @RequestBody EventRequestDTO eventDTO,
            Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).build();
        }

        String email = authentication.getName(); // Email desde el JWT
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Event event = eventService.createEvent(eventDTO, user); // 👈 pásale el usuario
        return ResponseEntity.ok(eventService.mapToDto(event));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDTO> updateEvent(@PathVariable Long id, @RequestBody EventRequestDTO dto, Authentication authentication) {
        String email = authentication.getName();
        User owner = userService.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Event updated = eventService.updateEvent(id, dto, owner);
        return ResponseEntity.ok(eventService.mapToDto(updated));
    }


}
