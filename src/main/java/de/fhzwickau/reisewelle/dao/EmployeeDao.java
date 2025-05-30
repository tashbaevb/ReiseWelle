package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.Employee;
import de.fhzwickau.reisewelle.model.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class EmployeeDao implements BaseDao<Employee> {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeDao.class);
    private final UserRoleDao userRoleDao = new UserRoleDao();

    @Override
    public List<Employee> findAll() throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, vorname, nachname, email, erstellt_am FROM Mitarbeiter";
        List<Employee> employees = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                employees.add(mapEmployee(rs));
            }
        }

        return employees;
    }

    @Override
    public Employee findById(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, vorname, nachname, email, erstellt_am FROM Mitarbeiter WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapEmployee(rs);
                }
            }
        }

        throw new IllegalArgumentException("Mitarbeiter mit ID " + id + " wurde nicht gefunden.");
    }

    @Override
    public void save(Employee employee) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        boolean isNew = employee.getId() == null;
        String sql = isNew
                ? "INSERT INTO Mitarbeiter (id, vorname, nachname, email, passwort, salt, user_role_id, erstellt_am) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                : "UPDATE Mitarbeiter SET vorname = ?, nachname = ?, email = ?, erstellt_am = ? WHERE id = ?";

        if (isNew) {
            employee.setId(UUID.randomUUID());
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (isNew) {
                prepareInsert(stmt, employee);
                logger.info("INSERT employee: " + employee.getEmail());
            } else {
                prepareUpdate(stmt, employee);
                logger.info("UPDATE employee: " + employee.getEmail());
            }

            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "DELETE FROM Mitarbeiter WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        }
    }

    private Employee mapEmployee(ResultSet rs) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        String vorname = rs.getString("vorname");
        String nachname = rs.getString("nachname");
        String email = rs.getString("email");
        LocalDateTime erstelltAm = rs.getTimestamp("erstellt_am").toLocalDateTime();

        UserRole role = userRoleDao.findByName("Employee");

        Employee employee = new Employee(vorname, nachname, email, erstelltAm);
        employee.setId(id);
        employee.setUserRole(role);

        return employee;
    }

    private void prepareInsert(PreparedStatement stmt, Employee employee) throws SQLException {
        stmt.setString(1, employee.getId().toString());
        stmt.setString(2, employee.getVorname());
        stmt.setString(3, employee.getNachname());
        stmt.setString(4, employee.getEmail());
        stmt.setString(5, employee.getPasswort());
        stmt.setString(6, employee.getSalt());
        stmt.setString(7, employee.getUserRole().getId().toString());
        stmt.setTimestamp(8, Timestamp.valueOf(employee.getErstelltAm() != null ? employee.getErstelltAm() : LocalDateTime.now()));
    }

    private void prepareUpdate(PreparedStatement stmt, Employee employee) throws SQLException {
        stmt.setString(1, employee.getVorname());
        stmt.setString(2, employee.getNachname());
        stmt.setString(3, employee.getEmail());
        stmt.setTimestamp(4, Timestamp.valueOf(employee.getErstelltAm() != null ? employee.getErstelltAm() : LocalDateTime.now()));
        stmt.setString(5, employee.getId().toString());
    }
}
