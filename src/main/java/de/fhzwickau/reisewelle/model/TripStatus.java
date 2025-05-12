package de.fhzwickau.reisewelle.model;

import java.util.UUID;

public class TripStatus {

    private UUID id;
    private String name;

    public TripStatus(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public TripStatus() {
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name != null ? name : "Unnamed Status";
    }
}
