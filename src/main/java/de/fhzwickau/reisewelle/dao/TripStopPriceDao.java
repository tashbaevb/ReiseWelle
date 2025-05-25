// TripStopPriceDao.java
package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.Stop;
import de.fhzwickau.reisewelle.model.TripStopPrice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class TripStopPriceDao implements BaseDao<TripStopPrice> {

    private final StopDao stopDao = new StopDao();


    /** Удаляет старые цены и вставляет новые для данного рейса */
    public void saveAllForTrip(UUID tripId, List<TripStopPrice> prices) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        try (PreparedStatement del = conn.prepareStatement(
                "DELETE FROM TripStopPrice WHERE trip_id = ?"
        )) {
            del.setString(1, tripId.toString());
            del.executeUpdate();
        }
        String sql = "INSERT INTO TripStopPrice " +
                "(id, trip_id, stop_id, price_from_start) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ins = conn.prepareStatement(sql)) {
            for (TripStopPrice p : prices) {
                if (p.getId() == null) p.setId(UUID.randomUUID());
                ins.setString(1, p.getId().toString());
                ins.setString(2, tripId.toString());
                ins.setString(3, p.getStop().getId().toString());
                ins.setDouble(4, p.getPriceFromStart());
                ins.addBatch();
            }
            ins.executeBatch();
        }
    }

    // --- BaseDao methods ---

    @Override
    public TripStopPrice findById(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, trip_id, stop_id, price_from_start FROM TripStopPrice WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TripStopPrice p = new TripStopPrice(null, null, rs.getDouble("price_from_start"));
                    p.setId(id);
                    return p;
                }
            }
        }
        return null;
    }

    @Override
    public List<TripStopPrice> findAll() throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, trip_id, stop_id, price_from_start FROM TripStopPrice";
        List<TripStopPrice> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                TripStopPrice p = new TripStopPrice(null, null, rs.getDouble("price_from_start"));
                p.setId(UUID.fromString(rs.getString("id")));
                list.add(p);
            }
        }
        return list;
    }

    @Override
    public void save(TripStopPrice entity) throws SQLException {
        saveAllForTrip(entity.getTrip().getId(), List.of(entity));
    }

    @Override
    public void delete(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM TripStopPrice WHERE id = ?"
        )) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        }
    }

    /** Вспомогательный метод для загрузки всех цен одного рейса */
    public List<TripStopPrice> findByTripId(UUID tripId) throws SQLException {
        // First load all stops for this trip
        List<Stop> stops = stopDao.findByTripId(tripId);
        Map<UUID, Stop> stopMap = stops.stream()
                .collect(Collectors.toMap(Stop::getId, s -> s));

        // Then load price entries
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, stop_id, price_from_start " +
                "FROM TripStopPrice WHERE trip_id = ?";
        List<TripStopPrice> list = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tripId.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UUID priceId = UUID.fromString(rs.getString("id"));
                    UUID stopId  = UUID.fromString(rs.getString("stop_id"));
                    double price = rs.getDouble("price_from_start");

                    Stop stop = stopMap.get(stopId);
                    // stop must not be null now
                    TripStopPrice p = new TripStopPrice(null, stop, price);
                    p.setId(priceId);
                    list.add(p);
                }
            }
        }
        return list;
    }
}
