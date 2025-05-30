package de.fhzwickau.reisewelle.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Employee {

    private UUID id;
    private String vorname, nachname;
    private String email, passwort, salt;
    private LocalDateTime erstelltAm;
    private UserRole userRole;

    public Employee(String vorname, String nachname, String email, String passwort, String salt , UserRole userRole) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.email = email;
        this.passwort = passwort;
        this.salt = salt;
        this.userRole = userRole;
        erstelltAm = LocalDateTime.now();
    }

    public Employee(String vorname, String nachname, String email, LocalDateTime erstelltAm) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.email = email;
        this.erstelltAm = erstelltAm;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswort() {
        return passwort;
    }

    public void setPasswort(String password) {
        this.passwort = passwort;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public LocalDateTime getErstelltAm() {
        return erstelltAm;
    }

    public void setErstelltAm(LocalDateTime erstelltAm) {
        this.erstelltAm = erstelltAm;
    }
}
