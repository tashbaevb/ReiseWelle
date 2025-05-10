package de.fhzwickau.reisewelle.model;

import java.util.UUID;

public class DriverStatus {

    private UUID id;
    private String statusName;

    public DriverStatus(String statusName) {
        this.id = UUID.randomUUID();
        this.statusName = statusName;
    }

    public UUID getId() {
        return id;
    }
    public String getStatusName() {
        return statusName;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

}
