package de.fhzwickau.reisewelle.model;

import java.util.UUID;

public interface Authenticatable {

    String getEmail();

    String getPassword();

    String getSalt();

    UserRole getUserRole();

    UUID getId();
}
