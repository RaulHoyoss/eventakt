package com.example.backend.model.dto;

public class UserDto {
    private String name;
    private String email;
    private String phone;
    private String profileImagePath;

    public UserDto() {
    }

    public UserDto(String name, String email, String phone, String profileImagePath) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.profileImagePath = profileImagePath;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }
}
