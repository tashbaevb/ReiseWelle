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

public class TripStatusDao {

    public List<TripStatus> findAll() throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        List<TripStatus> tripStatuses = new ArrayList<>();
        String sql = "SELECT id, name FROM TripStatus";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                TripStatus tripStatus = new TripStatus(rs.getString("name"));
                tripStatus.setId(UUID.fromString(rs.getString("id")));
                tripStatuses.add(tripStatus);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tripStatuses;
    }

    public TripStatus findById(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, name FROM TripStatus WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TripStatus tripStatus = new TripStatus(rs.getString("name"));
                    tripStatus.setId(UUID.fromString(rs.getString("id")));
                    return tripStatus;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(TripStatus tripStatus) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = tripStatus.getId() == null ?
                "INSERT INTO TripStatus (id, name) VALUES (?, ?)" :
                "UPDATE TripStatus SET name = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (tripStatus.getId() == null) {
                tripStatus.setId(UUID.randomUUID());
                stmt.setString(1, tripStatus.getId().toString());
                stmt.setString(2, tripStatus.getName());
            } else {
                stmt.setString(1, tripStatus.getName());
                stmt.setString(2, tripStatus.getId().toString());
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "DELETE FROM TripStatus WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}