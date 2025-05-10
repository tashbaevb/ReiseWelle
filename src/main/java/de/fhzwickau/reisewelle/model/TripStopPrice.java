package de.fhzwickau.reisewelle.model;

import java.util.UUID;

public class TripStopPrice {

    private UUID id;
    private Trip trip;
    private Stop stop;
    private Double priceFromStart;

    public TripStopPrice(Trip trip, Stop stop, Double priceFromStart) {
        this.id = UUID.randomUUID();
        this.trip = trip;
        this.stop = stop;
        this.priceFromStart = priceFromStart;
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

    public Stop getStop() {
        return stop;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
    }

    public Double getPriceFromStart() {
        return priceFromStart;
    }

    public void setPriceFromStart(Double priceFromStart) {
        this.priceFromStart = priceFromStart;
    }
}
