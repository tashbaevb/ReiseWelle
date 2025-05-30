package de.fhzwickau.reisewelle.model;

import java.util.UUID;

public class TripStopPrice {

    private UUID id;
    private Trip trip;
    private Stop stop;
    private Double priceFromStartAdult;
    private Double priceFromStartChild;

    public TripStopPrice(Trip trip, Stop stop, Double priceFromStartAdult, Double priceFromStartChild) {
        this.id = UUID.randomUUID();
        this.trip = trip;
        this.stop = stop;
        this.priceFromStartAdult = priceFromStartAdult;
        this.priceFromStartChild = priceFromStartChild;
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

    public Double getPriceFromStartAdult() {
        return priceFromStartAdult;
    }

    public void setPriceFromStartAdult(Double priceFromStartAdult) {
        this.priceFromStartAdult = priceFromStartAdult;
    }

    public Double getPriceFromStartChild() {
        return priceFromStartChild;
    }

    public void setPriceFromStartChild(Double priceFromStartChild) {
        this.priceFromStartChild = priceFromStartChild;
    }
}
