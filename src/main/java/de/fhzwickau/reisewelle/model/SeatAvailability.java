package de.fhzwickau.reisewelle.model;

import java.util.UUID;

public class SeatAvailability {

    private UUID id;
    private Trip trip;
    private Stop startStop;
    private Stop endStop;
    private Integer availableSeats;

    public SeatAvailability(Trip trip, Stop startStop, Stop endStop, int availableSeats) {
        this.id = UUID.randomUUID();
        this.trip = trip;
        this.startStop = startStop;
        this.endStop = endStop;
        this.availableSeats = availableSeats;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Stop getStartStop() {
        return startStop;
    }

    public void setStartStop(Stop startStop) {
        this.startStop = startStop;
    }

    public Stop getEndStop() {
        return endStop;
    }

    public void setEndStop(Stop endStop) {
        this.endStop = endStop;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }
}
