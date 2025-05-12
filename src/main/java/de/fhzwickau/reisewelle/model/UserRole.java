package de.fhzwickau.reisewelle.model;

import java.util.UUID;

public class UserRole {

    private UUID id;
    private String roleName;

    public UserRole(String roleName) {
        this.roleName = roleName;
    }

    public UUID getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    @Override
    public String toString() {
        return roleName != null ? roleName : "Unnamed Status";
    }

}
