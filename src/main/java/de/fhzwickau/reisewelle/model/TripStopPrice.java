package de.fhzwickau.reisewelle.model;

import java.util.UUID;

public class TripStopPrice {

    private UUID id;
    private UUID tripId;
    private UUID stopId;
    private double priceFromStart;

    public TripStopPrice(UUID tripId, UUID stopId, double priceFromStart) {
        this.id = UUID.randomUUID();
        this.tripId = tripId;
        this.stopId = stopId;
        this.priceFromStart = priceFromStart;
    }
    public UUID getId() {
        return id;
    }
    public UUID getTripId() {
        return tripId;
    }
    public UUID getStopId() {
        return stopId;
    }
    public double getPriceFromStart() {
        return priceFromStart;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setTripId(UUID tripId) {
        this.tripId = tripId;
    }
    public void setStopId(UUID stopId) {
        this.stopId = stopId;
    }
    public void setPriceFromStart(double priceFromStart) {
        this.priceFromStart = priceFromStart;
    }
}
