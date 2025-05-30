package de.fhzwickau.reisewelle.dto;

import java.util.List;

public class TripDetailsDTO {

    private String busNumber;
    private List<String> stops;
    private Double price;

    public TripDetailsDTO(String busNumber, List<String> stops, Double price) {
        this.busNumber = busNumber;
        this.stops = stops;
        this.price = price;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public List<String> getStops() {
        return stops;
    }

    public void setStops(List<String> stops) {
        this.stops = stops;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
