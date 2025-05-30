package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.SeatAvailability;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SeatAvailabilityDao implements BaseDao<SeatAvailability> {

    private final StopDao stopDao = new StopDao();

    @Override
    public List<SeatAvailability> findAll() throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, trip_id, start_stop_id, end_stop_id, available_seats, available_bicycle_spaces FROM SeatAvailability";
        List<SeatAvailability> list = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public SeatAvailability findById(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT trip_id, start_stop_id, end_stop_id, available_seats, available_bicycle_spaces FROM SeatAvailability WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    SeatAvailability sa = mapRow(rs);
                    sa.setId(id);
                    return sa;
                }
            }
        }
        return null;
    }

    @Override
    public void save(SeatAvailability sa) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        boolean isNew = sa.getId() == null;
        String sql = isNew
                ? "INSERT INTO SeatAvailability (id, trip_id, start_stop_id, end_stop_id, available_seats, available_bicycle_spaces) VALUES (?, ?, ?, ?, ?, ?)"
                : "UPDATE SeatAvailability SET trip_id=?, start_stop_id=?, end_stop_id=?, available_seats=?, available_bicycle_spaces=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int i = 1;
            if (isNew) {
                sa.setId(UUID.randomUUID());
                stmt.setString(i++, sa.getId().toString());
            }
            stmt.setString(i++, sa.getTrip().getId().toString());
            stmt.setString(i++, sa.getStartStop().getId().toString());
            stmt.setString(i++, sa.getEndStop().getId().toString());
            stmt.setInt(i++, sa.getAvailableSeats());
            stmt.setInt(i++, sa.getAvailableBicycleSeats());
            if (!isNew) stmt.setString(i, sa.getId().toString());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM SeatAvailability WHERE id = ?")) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        }
    }

    public void deleteAllForTrip(UUID tripId) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM SeatAvailability WHERE trip_id = ?")) {
            stmt.setString(1, tripId.toString());
            stmt.executeUpdate();
        }
    }

    public void deleteAllForStop(UUID stopId) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM SeatAvailability WHERE start_stop_id = ? OR end_stop_id = ?")) {
            stmt.setString(1, stopId.toString());
            stmt.setString(2, stopId.toString());
            stmt.executeUpdate();
        }
    }

    private SeatAvailability mapRow(ResultSet rs) throws SQLException {
        UUID tripId = UUID.fromString(rs.getString("trip_id"));
        UUID startId = UUID.fromString(rs.getString("start_stop_id"));
        UUID endId = UUID.fromString(rs.getString("end_stop_id"));

        TripAdminDao tripDao = new TripAdminDao();

        return new SeatAvailability(
                tripDao.findById(tripId),
                stopDao.findById(startId),
                stopDao.findById(endId),
                rs.getInt("available_seats"),
                rs.getInt("available_bicycle_spaces")
        );
    }
}
