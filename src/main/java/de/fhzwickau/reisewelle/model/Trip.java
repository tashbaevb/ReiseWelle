package de.fhzwickau.reisewelle.model;

import java.util.UUID;
import java.time.LocalDate;

public class Trip {

    private UUID id;
    private Bus bus;
    private Driver driver;
    private LocalDate departureDate;
    private TripStatus status;

    public Trip(Bus bus, Driver driver, LocalDate departureDate, TripStatus status) {
        this.id = UUID.randomUUID();
        this.bus = bus;
        this.driver = driver;
        this.departureDate = departureDate;
        this.status = status;
    }

    public Trip() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
    }
}
