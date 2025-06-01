package de.fhzwickau.reisewelle.model;

import java.util.UUID;

public class UserRolePermission {

    private UUID id;
    private UserRole userRole;
    private Permission permission;

    public UserRolePermission(UserRole userRole, Permission permission) {
        this.userRole = userRole;
        this.permission = permission;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }
}
