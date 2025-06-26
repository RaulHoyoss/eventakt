package com.example.backend.model.dto;

import java.time.LocalDateTime;
import java.util.List;

public class EventResponseDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime start;
    private LocalDateTime end;
    private String category;
    private List<ContactDTO> contacts;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getStart() { return start; }
    public void setStart(LocalDateTime start) { this.start = start; }

    public LocalDateTime getEnd() { return end; }
    public void setEnd(LocalDateTime end) { this.end = end; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }



    // Después
    public List<ContactDTO> getContacts() { return contacts; }
    public void setContacts(List<ContactDTO> contacts) { this.contacts = contacts; }

}

