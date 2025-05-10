package de.fhzwickau.reisewelle.model;

import java.util.UUID;

public class Driver {

    private UUID id;
    private String firstName;
    private String lastName;
    private String licenseNumber;
    private UUID statusId;

    public Driver(String firstName, String lastName, String licenseNumber, UUID statusId) {
        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.licenseNumber = licenseNumber;
        this.statusId = statusId;
    }

    public UUID getId() {
        return id;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getLicenseNumber() {
        return licenseNumber;
    }
    public UUID getStatusId() {
        return statusId;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
    public void setStatusId(UUID statusId) {
        this.statusId = statusId;
    }
}
