package de.fhzwickau.reisewelle.model;

import java.util.UUID;
import java.time.LocalDateTime;

public class Stop {

    private UUID id;
    private Trip trip;
    private City city;
    private LocalDateTime arrivalTime;
    private LocalDateTime departureTime;
    private Integer stopOrder;

    public Stop(Trip trip, City city, LocalDateTime arrivalTime, LocalDateTime departureTime, Integer stopOrder) {
        this.id = UUID.randomUUID();
        this.trip = trip;
        this.city = city;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.stopOrder = stopOrder;
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

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public Integer getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(Integer stopOrder) {
        this.stopOrder = stopOrder;
    }
}
