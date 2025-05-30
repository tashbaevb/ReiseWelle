package de.fhzwickau.reisewelle.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class TripSegmentDTO {

    private UUID tripId;
    private String fromCity;
    private String toCity;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Double price;
    private Integer availableSeats;
    private Integer availableBicycleSpaces;
    private UUID startStopId;
    private UUID endStopId;

    public TripSegmentDTO(UUID tripId, String fromCity, String toCity, LocalDateTime departureTime, LocalDateTime arrivalTime, Double price, Integer availableSeats, Integer availableBicycleSpaces, UUID startStopId, UUID endStopId) {
        this.tripId = tripId;
        this.fromCity = fromCity;
        this.toCity = toCity;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.availableSeats = availableSeats;
        this.availableBicycleSpaces = availableBicycleSpaces;
        this.startStopId = startStopId;
        this.endStopId = endStopId;
    }

    public UUID getTripId() {
        return tripId;
    }

    public void setTripId(UUID tripId) {
        this.tripId = tripId;
    }

    public String getFromCity() {
        return fromCity;
    }

    public void setFromCity(String fromCity) {
        this.fromCity = fromCity;
    }

    public String getToCity() {
        return toCity;
    }

    public void setToCity(String toCity) {
        this.toCity = toCity;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Integer getAvailableBicycleSpaces() {
        return availableBicycleSpaces;
    }

    public void setAvailableBicycleSpaces(Integer availableBicycleSpaces) {
        this.availableBicycleSpaces = availableBicycleSpaces;
    }

    public UUID getStartStopId() {
        return startStopId;
    }

    public void setStartStopId(UUID startStopId) {
        this.startStopId = startStopId;
    }

    public UUID getEndStopId() {
        return endStopId;
    }

    public void setEndStopId(UUID endStopId) {
        this.endStopId = endStopId;
    }
}
