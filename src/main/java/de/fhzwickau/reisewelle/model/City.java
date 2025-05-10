package de.fhzwickau.reisewelle.model;

import java.util.UUID;

public class City {

    private UUID id;
    private String name;
    private Country country;

    public City(String name, Country country) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.country = country;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
