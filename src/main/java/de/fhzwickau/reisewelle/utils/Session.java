package de.fhzwickau.reisewelle.utils;

import de.fhzwickau.reisewelle.model.User;

public class Session {

    private static Session instance;
    private User currentUser;

    private Session() {
    }

    public static synchronized Session getInstance() {
        if (instance == null) instance = new Session();
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void clear() {
        currentUser = null;
    }
}
