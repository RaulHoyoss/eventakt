package com.example.backend.service;

import com.example.backend.model.Contact;
import com.example.backend.model.Event;
import com.example.backend.model.User;
import com.example.backend.model.dto.ContactDTO;
import com.example.backend.model.dto.EventDTO;
import com.example.backend.model.dto.EventRequestDTO;
import com.example.backend.model.dto.EventResponseDTO;
import com.example.backend.repository.ContactRepository;
import com.example.backend.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final ContactRepository contactRepository;
    public EventService(EventRepository eventRepository, ContactRepository contactRepository) {
        this.eventRepository = eventRepository;
        this.contactRepository = contactRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getEventsByUserId(Long userId) {
        return eventRepository.findByUserId(userId);
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event saveEvent(Event event) {
        List<Contact> resolvedContacts = event.getContacts().stream()
                .map(c -> contactRepository.findByEmail(c.getEmail())
                        .orElseThrow(() -> new RuntimeException("Contact not found: " + c.getEmail())))
                .collect(Collectors.toList());

        event.setContacts(resolvedContacts);

        return eventRepository.save(event);
    }


    public Event updateEvent(Long id, EventRequestDTO dto, User owner) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + id));

        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setStartTime(dto.getStart());
        event.setEndTime(dto.getEnd());
        event.setCategory(dto.getCategory());
        event.setUser(owner);

        List<Contact> resolvedContacts = dto.getContacts().stream()
                .map(email -> contactRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Contacto no encontrado: " + email)))
                .collect(Collectors.toList());

        event.setContacts(resolvedContacts);

        return eventRepository.save(event);
    }


    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public Event createEvent(EventRequestDTO dto, User user) {
        Event event = new Event();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setStartTime(dto.getStart());
        event.setEndTime(dto.getEnd());
        event.setCategory(dto.getCategory());
        event.setUser(user);

        // Convertir los emails en Contactos
        List<Contact> resolvedContacts = dto.getContacts().stream()
                .map(email -> contactRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Contact not found: " + email)))
                .collect(Collectors.toList());

        event.setContacts(resolvedContacts);

        return eventRepository.save(event);
    }

    public EventResponseDTO mapToDto(Event event) {
        EventResponseDTO dto = new EventResponseDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setStart(event.getStartTime());
        dto.setEnd(event.getEndTime());
        dto.setCategory(event.getCategory());

        // Convertimos la lista de Contact a ContactDTO
        List<ContactDTO> contactDTOs = event.getContacts().stream()
                .map(contact -> new ContactDTO(contact.getName(), contact.getEmail()))
                .toList();

        dto.setContacts(contactDTOs);

        return dto;
    }

}
