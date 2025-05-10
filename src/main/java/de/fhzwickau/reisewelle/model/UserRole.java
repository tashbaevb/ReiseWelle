package de.fhzwickau.reisewelle.model;

import java.util.UUID;

public class UserRole {

    private UUID id;
    private String role;

    public UserRole(String role) {
        this.id = UUID.randomUUID();
        this.role = role;
    }

    public UUID getId() {
        return id;
    }
    public String getRole() {
        return role;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setRole(String role) {
        this.role = role;
    }
}
