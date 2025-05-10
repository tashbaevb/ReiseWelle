package de.fhzwickau.reisewelle.model;

import java.util.UUID;

public class Driver {

    private UUID id;
    private String firstName;
    private String lastName;
    private String licenseNumber;
    private Status status;

    public Driver(String firstName, String lastName, String licenseNumber, Status status) {
        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.licenseNumber = licenseNumber;
        this.status = status;
    }

    public Driver() {
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

    public Status getStatus() {
        return status;
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

    public void setStatus(Status status) {
        this.status = status;
    }
}
