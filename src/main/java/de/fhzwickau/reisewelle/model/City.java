package de.fhzwickau.reisewelle.model;

import java.util.UUID;

public class City {

    private UUID id;
    private String name;
    private UUID countryId;

    public City(String name, UUID countryId) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.countryId = countryId;
    }

    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public UUID getCountryId() {
        return countryId;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCountryId(UUID countryId) {
        this.countryId = countryId;
    }
}
