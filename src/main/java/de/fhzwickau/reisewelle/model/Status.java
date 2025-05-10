package de.fhzwickau.reisewelle.model;

import java.util.UUID;

public class Status {

    private UUID id;
    private String name;

    public Status(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getname() {
        return name;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setname(String name) {
        this.name = name;
    }

}
