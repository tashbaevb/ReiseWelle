package de.fhzwickau.reisewelle.model;

import java.util.UUID;

public class Bus {
    private UUID id;
    private String busNumber;
    private int totalSeats;
    private Status status;
    private int bikeSpaces;    // new field

    public Bus(String busNumber, int totalSeats, Status status, int bikeSpaces) {
        this.id = UUID.randomUUID();
        this.busNumber = busNumber;
        this.totalSeats = totalSeats;
        this.status = status;
        this.bikeSpaces = bikeSpaces;
    }

    public Bus() { }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getBikeSpaces() {
        return bikeSpaces;
    }

    public void setBikeSpaces(int bikeSpaces) {
        this.bikeSpaces = bikeSpaces;
    }
}
