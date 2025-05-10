package de.fhzwickau.reisewelle.model;

import java.util.UUID;
import java.time.LocalDate;

public class Trip {

    private UUID id;
    private UUID busId;
    private UUID driverId;
    private LocalDate departureDate;
    private UUID statusId;

    public Trip(UUID busId, UUID driverId, LocalDate departureDate, UUID statusId) {
        this.id = UUID.randomUUID();
        this.busId = busId;
        this.driverId = driverId;
        this.departureDate = departureDate;
        this.statusId = statusId;
    }

    public UUID getId() {
        return id;
    }
    public UUID getBusId() {
        return busId;
    }
    public UUID getDriverId() {
        return driverId;
    }
    public LocalDate getDepartureDate() {
        return departureDate;
    }
    public UUID getStatusId() {
        return statusId;
    }

    public void setId(UUID id) {
        this.id = id;
    }
    public void setBusId(UUID busId) {
        this.busId = busId;
    }
    public void setDriverId(UUID driverId) {
        this.driverId = driverId;
    }
    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }
    public void setStatusId(UUID statusId) {
        this.statusId = statusId;
    }
}
