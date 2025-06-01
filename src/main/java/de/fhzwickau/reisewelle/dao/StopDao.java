package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.City;
import de.fhzwickau.reisewelle.model.Stop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StopDao implements BaseDao<Stop> {
    private final CityDao cityDao = new CityDao();

    @Override
    public Stop findById(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT city_id, arrival_time, departure_time, stop_order FROM Stop WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    City city = cityDao.findById(UUID.fromString(rs.getString("city_id")));
                    Stop s = new Stop(
                            null,
                            city,
                            rs.getTimestamp("arrival_time").toLocalDateTime(),
                            rs.getTimestamp("departure_time").toLocalDateTime(),
                            rs.getInt("stop_order")
                    );
                    s.setId(id);
                    return s;
                }
            }
        }
        return null;
    }

    @Override
    public List<Stop> findAll() throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, city_id, arrival_time, departure_time, stop_order FROM Stop";
        List<Stop> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                UUID id = UUID.fromString(rs.getString("id"));
                City city = cityDao.findById(UUID.fromString(rs.getString("city_id")));
                Stop s = new Stop(
                        null,
                        city,
                        rs.getTimestamp("arrival_time").toLocalDateTime(),
                        rs.getTimestamp("departure_time").toLocalDateTime(),
                        rs.getInt("stop_order")
                );
                s.setId(id);
                list.add(s);
            }
        }
        return list;
    }

    @Override
    public void save(Stop entity) throws SQLException {
        saveAllForTrip(entity.getTrip().getId(), List.of(entity));
    }

    public void saveAllForTrip(UUID tripId, List<Stop> stops) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        try (PreparedStatement del = conn.prepareStatement("DELETE FROM Stop WHERE trip_id = ?")) {
            del.setString(1, tripId.toString());
            del.executeUpdate();
        }

        String ins = """
                INSERT INTO Stop (id, trip_id, city_id, arrival_time, departure_time, stop_order)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement stmt = conn.prepareStatement(ins)) {
            for (Stop s : stops) {
                if (s.getId() == null) {
                    s.setId(UUID.randomUUID());
                }
                stmt.setString(1, s.getId().toString());
                stmt.setString(2, tripId.toString());
                stmt.setString(3, s.getCity().getId().toString());
                stmt.setTimestamp(4, Timestamp.valueOf(s.getArrivalTime()));
                stmt.setTimestamp(5, Timestamp.valueOf(s.getDepartureTime()));
                stmt.setInt(6, s.getStopOrder());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    @Override
    public void delete(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Stop WHERE id = ?")) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        }
    }

    public void deleteAllForTrip(UUID tripId) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Stop WHERE trip_id = ?")) {
            stmt.setString(1, tripId.toString());
            stmt.executeUpdate();
        }
    }

    public List<Stop> findByTripId(UUID tripId) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, city_id, arrival_time, departure_time, stop_order FROM Stop WHERE trip_id = ? ORDER BY stop_order";
        List<Stop> stops = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tripId.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UUID id = UUID.fromString(rs.getString("id"));
                    City city = cityDao.findById(UUID.fromString(rs.getString("city_id")));
                    Stop s = new Stop(
                            null,
                            city,
                            rs.getTimestamp("arrival_time").toLocalDateTime(),
                            rs.getTimestamp("departure_time").toLocalDateTime(),
                            rs.getInt("stop_order")
                    );
                    s.setId(id);
                    stops.add(s);
                }
            }
        }
        return stops;
    }

    public List<Stop> findAllByTripOrdered(UUID tripId) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        List<Stop> stops = new ArrayList<>();
        String sql = "SELECT * FROM Stop WHERE trip_id = ? ORDER BY stop_order ASC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tripId.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UUID id = UUID.fromString(rs.getString("id"));
                City city = cityDao.findById(UUID.fromString(rs.getString("city_id")));
                Stop s = new Stop(
                        null,
                        city,
                        rs.getTimestamp("arrival_time").toLocalDateTime(),
                        rs.getTimestamp("departure_time").toLocalDateTime(),
                        rs.getInt("stop_order")
                );
                s.setId(id);
                stops.add(s);
            }
        }
        return stops;
    }
}
