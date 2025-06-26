package com.example.backend.model.dto;

public class ContactDTO {
    private String name;
    private String email;

    // Constructor vacío
    public ContactDTO() {}

    // Constructor completo (opcional)
    public ContactDTO(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters y setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
