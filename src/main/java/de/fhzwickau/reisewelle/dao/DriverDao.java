package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.Driver;
import de.fhzwickau.reisewelle.model.Status;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DriverDao implements BaseDao<Driver> {

    private final StatusDao statusDao = new StatusDao();

    @Override
    public List<Driver> findAll() throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, first_name, last_name, license_number, status_id FROM Driver";
        List<Driver> drivers = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                drivers.add(mapRowToDriver(rs));
            }
        }
        return drivers;
    }

    @Override
    public Driver findById(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, first_name, last_name, license_number, status_id FROM Driver WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToDriver(rs);
                }
            }
        }
        return null;
    }

    @Override
    public void save(Driver driver) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        boolean isNew = driver.getId() == null;

        String sql = isNew
                ? "INSERT INTO Driver (id, first_name, last_name, license_number, status_id) VALUES (?, ?, ?, ?, ?)"
                : "UPDATE Driver SET first_name = ?, last_name = ?, license_number = ?, status_id = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (isNew) {
                prepareInsert(stmt, driver);
            } else {
                prepareUpdate(stmt, driver);
            }
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "DELETE FROM Driver WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        }
    }

    private Driver mapRowToDriver(ResultSet rs) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String licenseNumber = rs.getString("license_number");
        UUID statusId = UUID.fromString(rs.getString("status_id"));
        Status status = statusDao.findById(statusId);

        Driver driver = new Driver(firstName, lastName, licenseNumber, status);
        driver.setId(id);
        return driver;
    }

    private void prepareInsert(PreparedStatement stmt, Driver driver) throws SQLException {
        driver.setId(UUID.randomUUID());
        stmt.setString(1, driver.getId().toString());
        stmt.setString(2, driver.getFirstName());
        stmt.setString(3, driver.getLastName());
        stmt.setString(4, driver.getLicenseNumber());
        stmt.setString(5, driver.getStatus().getId().toString());
    }

    private void prepareUpdate(PreparedStatement stmt, Driver driver) throws SQLException {
        stmt.setString(1, driver.getFirstName());
        stmt.setString(2, driver.getLastName());
        stmt.setString(3, driver.getLicenseNumber());
        stmt.setString(4, driver.getStatus().getId().toString());
        stmt.setString(5, driver.getId().toString());
    }
}
