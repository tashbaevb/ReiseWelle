package de.fhzwickau.reisewelle.utils;

import de.fhzwickau.reisewelle.model.Authenticatable;
import de.fhzwickau.reisewelle.model.User;

public class Session {

    private static Session instance;
    private Authenticatable currentUser;

    private Session() {
    }

    public static synchronized Session getInstance() {
        if (instance == null) instance = new Session();
        return instance;
    }

    public Authenticatable getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Authenticatable currentUser) {
        this.currentUser = currentUser;
    }

    public void clear() {
        currentUser = null;
    }
}
