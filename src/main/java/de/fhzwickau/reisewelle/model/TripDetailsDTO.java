package de.fhzwickau.reisewelle.model;

import java.util.List;

public class TripDetailsDTO {

    private String driverName;
    private String busNumber;
    private Integer availableSeats;
    private List<String> stops;

    public TripDetailsDTO(String driverName, String busNumber, Integer availableSeats, List<String> stops) {
        this.driverName = driverName;
        this.busNumber = busNumber;
        this.availableSeats = availableSeats;
        this.stops = stops;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public List<String> getStops() {
        return stops;
    }

    public void setStops(List<String> stops) {
        this.stops = stops;
    }
}
