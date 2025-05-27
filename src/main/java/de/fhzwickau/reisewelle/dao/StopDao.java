// src/main/java/de/fhzwickau/reisewelle/dao/StopDao.java
package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.City;
import de.fhzwickau.reisewelle.model.Stop;

import java.sql.*;
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
                UUID id   = UUID.fromString(rs.getString("id"));
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
        // Для единичного сохранения просто оборачиваем batch-метод
        saveAllForTrip(entity.getTrip().getId(), List.of(entity));
    }

    /**
     * Полная перезапись всех остановок рейса: сначала удаляем старые,
     * потом batch-вставляем новые (с сохранением их UUID, если он уже был).
     */
    public void saveAllForTrip(UUID tripId, List<Stop> stops) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        // 1) удалить все старые остановки
        try (PreparedStatement del = conn.prepareStatement(
                "DELETE FROM Stop WHERE trip_id = ?"
        )) {
            del.setString(1, tripId.toString());
            del.executeUpdate();
        }

        // 2) batch-вставка
        String ins = """
            INSERT INTO Stop
              (id, trip_id, city_id, arrival_time, departure_time, stop_order)
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
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM Stop WHERE id = ?"
        )) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        }
    }

    /** Удалить все остановки данного рейса без вставки новых */
    public void deleteAllForTrip(UUID tripId) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM Stop WHERE trip_id = ?"
        )) {
            stmt.setString(1, tripId.toString());
            stmt.executeUpdate();
        }
    }

    /** Все остановки для данного рейса */
    public List<Stop> findByTripId(UUID tripId) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, city_id, arrival_time, departure_time, stop_order FROM Stop WHERE trip_id = ? ORDER BY stop_order";
        List<Stop> stops = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tripId.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UUID id   = UUID.fromString(rs.getString("id"));
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
}
