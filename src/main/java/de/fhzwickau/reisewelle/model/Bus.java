package de.fhzwickau.reisewelle.model;

import java.util.UUID;

public class Bus {

    private UUID id;
    private String name;
    private int totalSeats;
    private UUID statusId;

    public Bus(String name, int totalSeats, UUID statusId) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.totalSeats = totalSeats;
        this.statusId = statusId;
    }

    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getTotalSeats() {
        return totalSeats;
    }

    public UUID getStatusId() {
        return statusId;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }
    public void setStatusId(UUID statusId) {
        this.statusId = statusId;
    }


}
