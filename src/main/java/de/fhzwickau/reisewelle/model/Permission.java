package de.fhzwickau.reisewelle.model;

import java.util.UUID;

public class Permission {

    private UUID id;
    private String permissionName;

    public Permission(UUID id, String permissionName) {
        this.id = id;
        this.permissionName = permissionName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    @Override
    public String toString() {
        return permissionName;
    }
}
