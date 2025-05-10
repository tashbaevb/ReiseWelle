package de.fhzwickau.reisewelle.model;

import java.util.UUID;
import java.time.LocalDateTime;

public class Ticket {

    private UUID id;
    private UUID userId;
    private UUID tripId;
    private UUID startStopId;
    private UUID endStopId;
    private int adultCount;
    private int childCount;
    private int bikeCount;
    private double totalPrice;
    private LocalDateTime purchaseDate;


    public Ticket(UUID userId, UUID tripId, UUID startStopId, UUID endStopId, int adultCount, int childCount, int bikeCount, double totalPrice, LocalDateTime purchaseDate) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.tripId = tripId;
        this.startStopId = startStopId;
        this.endStopId = endStopId;
        this.adultCount = adultCount;
        this.childCount = childCount;
        this.bikeCount = bikeCount;
        this.totalPrice = totalPrice;
        this.purchaseDate = purchaseDate;
    }

    public UUID getId() {
        return id;
    }
    public UUID getUserId() {
        return userId;
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
    public int getAdultCount(){
        return adultCount;
    }
    public int getChildCount(){
        return childCount;
    }
    public int getBikeCount(){
        return bikeCount;
    }
    public double getTotalPrice(){
        return totalPrice;
    }
    public LocalDateTime getPurchaseDate(){
        return purchaseDate;
    }

    public void setId(UUID id) {
        this.id = id;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    public void setTripId(UUID tripId) {
        this.tripId = tripId;
    }
    public void setStartStopId(UUID startStopId) {
        this.startStopId = startStopId;
    }
    public void setEndStopId(UUID endStopId){
        this.endStopId = endStopId;
    }
    public void setAdultCount(int adultCount){
        this.adultCount = adultCount;
    }
    public void setChildCount(int childCount){
        this.childCount = childCount;
    }
    public void setBikeCount(int bikeCount){
        this.bikeCount = bikeCount;
    }
    public void setTotalPrice(double totalPrice){
        this.totalPrice = totalPrice;
    }
    public void setPurchaseDate(LocalDateTime purchaseDate){
        this.purchaseDate = purchaseDate;
    }

}
