package de.fhzwickau.reisewelle.model;

import java.util.UUID;

public class Bus {

    private UUID id;
    private String busNumber;
    private Integer totalSeats;
    private Integer bicycleSpaces;
    private Status status;

    public Bus(String busNumber, Integer totalSeats, Status status, Integer bicycleSpaces) {
        this.busNumber = busNumber;
        this.totalSeats = totalSeats;
        this.status = status;
        this.bicycleSpaces = bicycleSpaces;
    }

    public Bus(UUID id, String busNumber, Integer totalSeats, Status status, Integer bicycleSpaces) {
        this.id = id;
        this.busNumber = busNumber;
        this.totalSeats = totalSeats;
        this.status = status;
        this.bicycleSpaces = bicycleSpaces;
    }

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

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Integer getBicycleSpaces() {
        return bicycleSpaces;
    }

    public void setBicycleSpaces(Integer bicycleSpaces) {
        this.bicycleSpaces = bicycleSpaces;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return busNumber;
    }
}
