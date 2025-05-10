package de.fhzwickau.reisewelle.model;

import java.util.UUID;
import java.time.LocalDateTime;

public class Stop {

    private UUID id;
    private UUID tripId;
    private UUID cityId;
    private LocalDateTime arrivalTime;
    private LocalDateTime departureTime;
    private int stopOrder;

    public Stop(UUID tripId, UUID cityId, LocalDateTime arrivalTime, LocalDateTime departureTime, int stopOrder) {
        this.id = UUID.randomUUID();
        this.tripId = tripId;
        this.cityId = cityId;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.stopOrder = stopOrder;
    }

    public UUID getId() {
        return id;
    }
    public UUID getTripId() {
        return tripId;
    }
    public UUID getCityId() {
        return cityId;
    }
    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }
    public LocalDateTime getDepartureTime() {
        return departureTime;
    }
    public int getStopOrder() {
        return stopOrder;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setTripId(UUID tripId) {
        this.tripId = tripId;
    }
    public void setCityId(UUID cityId) {
        this.cityId = cityId;
    }
    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }
    public void setStopOrder(int stopOrder) {
        this.stopOrder = stopOrder;
    }
}
