package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.TripStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TripStatusDao implements BaseDao<TripStatus> {

    @Override
    public List<TripStatus> findAll() throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT * FROM TripStatus";
        List<TripStatus> tripStatuses = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                tripStatuses.add(mapTripStatus(rs));
            }
        }

        return tripStatuses;
    }

    @Override
    public TripStatus findById(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT * FROM TripStatus WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapTripStatus(rs);
                }
            }
        }

        return null;
    }

    @Override
    public void save(TripStatus tripStatus) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        boolean isNew = tripStatus.getId() == null;
        String sql = isNew
                ? "INSERT INTO TripStatus (id, name) VALUES (?, ?)"
                : "UPDATE TripStatus SET name = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (isNew) {
                tripStatus.setId(UUID.randomUUID());
                prepareInsert(stmt, tripStatus);
            } else {
                prepareUpdate(stmt, tripStatus);
            }
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "DELETE FROM TripStatus WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        }
    }

    private TripStatus mapTripStatus(ResultSet rs) throws SQLException {
        TripStatus tripStatus = new TripStatus(rs.getString("name"));
        tripStatus.setId(UUID.fromString(rs.getString("id")));
        return tripStatus;
    }

    private void prepareInsert(PreparedStatement stmt, TripStatus tripStatus) throws SQLException {
        stmt.setString(1, tripStatus.getId().toString());
        stmt.setString(2, tripStatus.getName());
    }

    private void prepareUpdate(PreparedStatement stmt, TripStatus tripStatus) throws SQLException {
        stmt.setString(1, tripStatus.getName());
        stmt.setString(2, tripStatus.getId().toString());
    }
}