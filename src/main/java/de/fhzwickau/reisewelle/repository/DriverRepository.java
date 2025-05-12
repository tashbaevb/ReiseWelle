package de.fhzwickau.reisewelle.repository;

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

public class DriverRepository {
    private final StatusRepository statusRepository = new StatusRepository();

    public List<Driver> findAll() {
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, license_number, status_id FROM Driver";

        try (Connection conn = JDBCConfig.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                UUID statusId = UUID.fromString(rs.getString("status_id"));
                Status status = statusRepository.findById(statusId);
                Driver driver = new Driver(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("license_number"),
                        status
                );
                driver.setId(UUID.fromString(rs.getString("id")));
                drivers.add(driver);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drivers;
    }

    public Driver findById(UUID id) {
        String sql = "SELECT id, first_name, last_name, license_number, status_id FROM Driver WHERE id = ?";
        try (Connection conn = JDBCConfig.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UUID statusId = UUID.fromString(rs.getString("status_id"));
                    Status status = statusRepository.findById(statusId);
                    Driver driver = new Driver(
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("license_number"),
                            status
                    );
                    driver.setId(UUID.fromString(rs.getString("id")));
                    return driver;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(Driver driver) {
        String sql = driver.getId() == null ?
                "INSERT INTO Driver (id, first_name, last_name, license_number, status_id) VALUES (?, ?, ?, ?, ?)" :
                "UPDATE Driver SET first_name = ?, last_name = ?, license_number = ?, status_id = ? WHERE id = ?";

        try (Connection conn = JDBCConfig.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (driver.getId() == null) {
                driver.setId(UUID.randomUUID());
                stmt.setString(1, driver.getId().toString());
                stmt.setString(2, driver.getFirstName());
                stmt.setString(3, driver.getLastName());
                stmt.setString(4, driver.getLicenseNumber());
                stmt.setString(5, driver.getStatus().getId().toString());
            } else {
                stmt.setString(1, driver.getFirstName());
                stmt.setString(2, driver.getLastName());
                stmt.setString(3, driver.getLicenseNumber());
                stmt.setString(4, driver.getStatus().getId().toString());
                stmt.setString(5, driver.getId().toString());
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(UUID id) {
        String sql = "DELETE FROM Driver WHERE id = ?";
        try (Connection conn = JDBCConfig.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
