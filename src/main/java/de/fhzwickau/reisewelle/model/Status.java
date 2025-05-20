package de.fhzwickau.reisewelle.model;

import java.util.UUID;

public class Status {

    private UUID id;
    private String name;

    public Status(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public Status(String name) {
        this.name = name;
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
