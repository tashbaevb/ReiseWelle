package de.fhzwickau.reisewelle.model;

import java.util.UUID;

public class SeatAvailability {

    private UUID id;
    private UUID tripId;
    private UUID startStopId;
    private UUID endStopId;
    private int availableSeats;

    public SeatAvailability(UUID tripId, UUID startStopId, UUID endStopId, int availableSeats) {
        this.id = UUID.randomUUID();
        this.tripId = tripId;
        this.startStopId = startStopId;
        this.endStopId = endStopId;
        this.availableSeats = availableSeats;
    }

    public UUID getId() {
        return id;
    }
    public UUID getTripId() {
        return tripId;
    }
    public UUID getStartStopId() {
        return startStopId;
    }
    public UUID getEndStopId() {
        return endStopId;
    }
    public int getAvailableSeats() {
        return availableSeats;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setTripId(UUID tripId) {
        this.tripId = tripId;
    }
    public void setStartStopId(UUID startStopId) {
        this.startStopId = startStopId;
    }
    public void setEndStopId(UUID endStopId) {
        this.endStopId = endStopId;
    }
    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }
}
