package de.fhzwickau.reisewelle.model;

import java.util.UUID;

public class SeatAvailability {

    private UUID id;
    private Trip trip;
    private Stop startStop;
    private Stop endStop;
    private Integer availableSeats;
    private Integer availableBicycleSeats;

    public SeatAvailability(Trip trip, Stop startStop, Stop endStop, Integer availableSeats, Integer availableBicycleSeats) {
        this.trip = trip;
        this.startStop = startStop;
        this.endStop = endStop;
        this.availableSeats = availableSeats;
        this.availableBicycleSeats = availableBicycleSeats;
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

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Integer getAvailableBicycleSeats() {
        return availableBicycleSeats;
    }

    public void setAvailableBicycleSeats(Integer availableBicycleSeats) {
        this.availableBicycleSeats = availableBicycleSeats;
    }
}
