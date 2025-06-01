package de.fhzwickau.reisewelle.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Employee implements Authenticatable {

    private UUID id;
    private String vorname, nachname;
    private String email, password, salt;
    private LocalDateTime erstelltAm;
    private UserRole userRole;

    public Employee(String vorname, String nachname, String email, String password, String salt , UserRole userRole) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.email = email;
        this.password = password;
        this.salt = salt;
        this.userRole = userRole;
        erstelltAm = LocalDateTime.now();
    }

    public Employee(UUID id, String vorname, String nachname, String email, String password, String salt, LocalDateTime erstelltAm, UserRole userRole) {
        this.id = id;
        this.vorname = vorname;
        this.nachname = nachname;
        this.email = email;
        this.password = password;
        this.salt = salt;
        this.erstelltAm = erstelltAm;
        this.userRole = userRole;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
