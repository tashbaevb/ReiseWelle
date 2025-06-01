package de.fhzwickau.reisewelle.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {

    private UUID id;
    private String email;
    private String password;
    private String salt;
    private UserRole userRole;
    private LocalDateTime created_at;

    public User(String email, String password, String salt, UserRole userRole, LocalDateTime created_at) {
        this.email = email;
        this.password = password;
        this.userRole = userRole;
        this.created_at = created_at;
        this.salt = salt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public LocalDateTime getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }
}
