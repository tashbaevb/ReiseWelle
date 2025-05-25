// StopDao.java
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


    /** Удаляет все остановки и вставляет новые */
    public void saveAllForTrip(UUID tripId, List<Stop> stops) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        try (PreparedStatement del = conn.prepareStatement(
                "DELETE FROM Stop WHERE trip_id = ?"
        )) {
            del.setString(1, tripId.toString());
            del.executeUpdate();
        }
        String insSql = "INSERT INTO Stop " +
                "(id, trip_id, city_id, arrival_time, departure_time, stop_order) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ins = conn.prepareStatement(insSql)) {
            for (Stop s : stops) {
                if (s.getId() == null) s.setId(UUID.randomUUID());
                ins.setString(1, s.getId().toString());
                ins.setString(2, tripId.toString());
                ins.setString(3, s.getCity().getId().toString());
                ins.setTimestamp(4, Timestamp.valueOf(s.getArrivalTime()));
                ins.setTimestamp(5, Timestamp.valueOf(s.getDepartureTime()));
                ins.setInt(6, s.getStopOrder());
                ins.addBatch();
            }
            ins.executeBatch();
        }
    }

    // --- BaseDao methods ---

    @Override
    public Stop findById(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, trip_id, city_id, arrival_time, departure_time, stop_order "
                + "FROM Stop WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Здесь можно полностью заполнять объект Stop,
                    // но для админки обычно используется findByTripId.
                    Stop s = new Stop(null, null, null, null, rs.getInt("stop_order"));
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
        String sql = "SELECT id, trip_id, city_id, arrival_time, departure_time, stop_order FROM Stop";
        List<Stop> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Stop s = new Stop(null, null, null, null, rs.getInt("stop_order"));
                s.setId(UUID.fromString(rs.getString("id")));
                list.add(s);
            }
        }
        return list;
    }

    @Override
    public void save(Stop entity) throws SQLException {
        // не используем одиночный save, а batch через saveAllForTrip
        saveAllForTrip(entity.getTrip().getId(), List.of(entity));
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

    /** Удобный метод для загрузки всех остановок одного рейса */
    public List<Stop> findByTripId(UUID tripId) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, city_id, arrival_time, departure_time, stop_order " +
                "FROM Stop WHERE trip_id = ? ORDER BY stop_order";
        List<Stop> stops = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tripId.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UUID id = UUID.fromString(rs.getString("id"));
                    UUID cityId = UUID.fromString(rs.getString("city_id"));
                    City city = cityDao.findById(cityId);
                    Timestamp at = rs.getTimestamp("arrival_time");
                    Timestamp dt = rs.getTimestamp("departure_time");
                    int order = rs.getInt("stop_order");

                    Stop s = new Stop(null, city,
                            at.toLocalDateTime(),
                            dt.toLocalDateTime(),
                            order);
                    s.setId(id);
                    stops.add(s);
                }
            }
        }
        return stops;
    }


}
