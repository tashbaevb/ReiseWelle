package de.fhzwickau.reisewelle.controller;

import de.fhzwickau.reisewelle.dao.EmployeeDao;
import de.fhzwickau.reisewelle.dao.UserDao;
import de.fhzwickau.reisewelle.model.Authenticatable;

import java.sql.SQLException;

public class AuthenticationService {

    private final UserDao userDao = new UserDao();
    private final EmployeeDao employeeDao = new EmployeeDao();

    public Authenticatable findByEmail(String email) throws SQLException {
        Authenticatable user = userDao.findByEmail(email);
        if (user != null) {
            return user;
        }

        return employeeDao.findByEmail(email);
    }
}
