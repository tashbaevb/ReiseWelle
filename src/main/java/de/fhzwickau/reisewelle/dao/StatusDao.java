package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.Status;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StatusDao implements BaseDao<Status> {

    @Override
    public List<Status> findAll() throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, name FROM Status";
        List<Status> statuses = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                statuses.add(mapStatus(rs));
            }
        }

        return statuses;
    }

    @Override
    public Status findById(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, name FROM Status WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapStatus(rs);
                }
            }
        }

        return null;
    }

    @Override
    public void save(Status status) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        boolean isNew = status.getId() == null;
        String sql = isNew
                ? "INSERT INTO Status (id, name) VALUES (?, ?)"
                : "UPDATE Status SET name = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (isNew) {
                status.setId(UUID.randomUUID());
                prepareInsert(stmt, status);
            } else {
                prepareUpdate(stmt, status);
            }
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(UUID id) throws SQLException {
        // Not Realized
    }

    private Status mapStatus(ResultSet rs) throws SQLException {
        Status status = new Status(rs.getString("name"));
        status.setId(UUID.fromString(rs.getString("id")));
        return status;
    }

    private void prepareInsert(PreparedStatement stmt, Status status) throws SQLException {
        stmt.setString(1, status.getId().toString());
        stmt.setString(2, status.getName());
    }

    private void prepareUpdate(PreparedStatement stmt, Status status) throws SQLException {
        stmt.setString(1, status.getName());
        stmt.setString(2, status.getId().toString());
    }
}