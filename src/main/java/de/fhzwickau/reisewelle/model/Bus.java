package de.fhzwickau.reisewelle.model;

import java.util.UUID;

public class Bus {

    private UUID id;
    private String busNumber;
    private Integer totalSeats;
    private Status status;

    public Bus(String busNumber, int totalSeats, Status status) {
        this.id = UUID.randomUUID();
        this.busNumber = busNumber;
        this.totalSeats = totalSeats;
        this.status = status;
    }

    public Bus() {
    }

    public UUID getId() {
        return id;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public Status getStatus() {
        return status;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setBusNumber(String name) {
        this.busNumber = name;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
