package de.fhzwickau.reisewelle.repository;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.Status;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StatusRepository {
    public List<Status> findAll() {
        List<Status> statuses = new ArrayList<>();
        String sql = "SELECT id, name FROM Status";

        try (Connection conn = JDBCConfig.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Status status = new Status(rs.getString("name"));
                status.setId(UUID.fromString(rs.getString("id")));
                statuses.add(status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statuses;
    }

    public Status findById(UUID id) {
        String sql = "SELECT id, name FROM Status WHERE id = ?";
        try (Connection conn = JDBCConfig.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Status status = new Status(rs.getString("name"));
                    status.setId(UUID.fromString(rs.getString("id")));
                    return status;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(Status status) {
        String sql = status.getId() == null ?
                "INSERT INTO Status (id, name) VALUES (?, ?)" :
                "UPDATE Status SET name = ? WHERE id = ?";

        try (Connection conn = JDBCConfig.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (status.getId() == null) {
                status.setId(UUID.randomUUID());
                stmt.setString(1, status.getId().toString());
                stmt.setString(2, status.getName());
            } else {
                stmt.setString(1, status.getName());
                stmt.setString(2, status.getId().toString());
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(UUID id) {
        String sql = "DELETE FROM Status WHERE id = ?";
        try (Connection conn = JDBCConfig.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
