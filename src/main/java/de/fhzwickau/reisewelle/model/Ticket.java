package de.fhzwickau.reisewelle.model;

import java.util.UUID;
import java.time.LocalDateTime;

public class Ticket {

    private UUID id;
    private User user;
    private Trip trip;
    private Stop startStop;
    private Stop endStop;
    private Integer adultCount;
    private Integer childCount;
    private Integer bikeCount;
    private Double totalPrice;
    private LocalDateTime purchaseDate;

    public Ticket(User user, Trip trip, Stop startStop, Stop endStop, Integer adultCount, Integer childCount, Integer bikeCount, Double totalPrice, LocalDateTime purchaseDate) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.trip = trip;
        this.startStop = startStop;
        this.endStop = endStop;
        this.adultCount = adultCount;
        this.childCount = childCount;
        this.bikeCount = bikeCount;
        this.totalPrice = totalPrice;
        this.purchaseDate = purchaseDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Integer getAdultCount() {
        return adultCount;
    }

    public void setAdultCount(Integer adultCount) {
        this.adultCount = adultCount;
    }

    public Integer getChildCount() {
        return childCount;
    }

    public void setChildCount(Integer childCount) {
        this.childCount = childCount;
    }

    public Integer getBikeCount() {
        return bikeCount;
    }

    public void setBikeCount(Integer bikeCount) {
        this.bikeCount = bikeCount;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}
