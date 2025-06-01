package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.Stop;
import de.fhzwickau.reisewelle.model.TripStopPrice;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class TripStopPriceDao implements BaseDao<TripStopPrice> {

    private final StopDao stopDao = new StopDao();

    public void saveAllForTrip(UUID tripId, List<TripStopPrice> prices) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        try (PreparedStatement del = conn.prepareStatement("DELETE FROM TripStopPrice WHERE trip_id = ?")) {
            del.setString(1, tripId.toString());
            del.executeUpdate();
        }
        String sql = "INSERT INTO TripStopPrice (id, trip_id, stop_id, price_from_start_adult, price_from_start_kinder) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ins = conn.prepareStatement(sql)) {
            for (TripStopPrice p : prices) {
                if (p.getId() == null) p.setId(UUID.randomUUID());
                ins.setString(1, p.getId().toString());
                ins.setString(2, tripId.toString());
                ins.setString(3, p.getStop().getId().toString());
                ins.setDouble(4, p.getPriceFromStartAdult());
                ins.setDouble(5, p.getPriceFromStartChild());
                ins.addBatch();
            }
            ins.executeBatch();
        }
    }

    public void deleteAllForTrip(UUID tripId) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM TripStopPrice WHERE trip_id = ?")) {
            stmt.setString(1, tripId.toString());
            stmt.executeUpdate();
        }
    }

    @Override
    public TripStopPrice findById(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT trip_id, stop_id, price_from_start_adult FROM TripStopPrice WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TripStopPrice p = new TripStopPrice(
                            null,
                            null,
                            rs.getDouble("price_from_start_adult"),
                            rs.getDouble("price_from_start_kinder")
                    );
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
        String sql = "SELECT id, trip_id, stop_id, price_from_start_adult FROM TripStopPrice";
        List<TripStopPrice> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                TripStopPrice p = new TripStopPrice(
                        null,
                        null,
                        rs.getDouble("price_from_start_adult"),
                        rs.getDouble("price_from_start_kinder")
                );
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
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM TripStopPrice WHERE id = ?")) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        }
    }

    public List<TripStopPrice> findByTripId(UUID tripId) throws SQLException {
        List<Stop> stops = stopDao.findByTripId(tripId);
        Map<UUID, Stop> stopMap = stops.stream()
                .collect(Collectors.toMap(Stop::getId, s -> s));

        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, stop_id, price_from_start_adult, price_from_start_kinder FROM TripStopPrice WHERE trip_id = ?";
        List<TripStopPrice> list = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tripId.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UUID priceId = UUID.fromString(rs.getString("id"));
                    UUID stopId = UUID.fromString(rs.getString("stop_id"));
                    TripStopPrice p = new TripStopPrice(null, stopMap.get(stopId), rs.getDouble("price_from_start_adult"), rs.getDouble("price_from_start_kinder"));
                    p.setId(priceId);
                    list.add(p);
                }
            }
        }
        return list;
    }

    public double getPriceAdult(UUID tripId, UUID fromStopId, UUID toStopId) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = """
                    SELECT 
                        (pto.price_from_start_adult - pfrom.price_from_start_adult) AS price
                    FROM TripStopPrice pto
                    JOIN TripStopPrice pfrom ON pto.trip_id = pfrom.trip_id
                    WHERE pto.trip_id = ? AND pfrom.trip_id = ?
                      AND pto.stop_id = ? AND pfrom.stop_id = ?
                """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tripId.toString());
            stmt.setString(2, tripId.toString());
            stmt.setString(3, toStopId.toString());
            stmt.setString(4, fromStopId.toString());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("price");
            }
        }
        throw new SQLException("Preis nicht gefunden für Stops: " + fromStopId + " → " + toStopId);
    }

    public double getPriceChild(UUID tripId, UUID fromStopId, UUID toStopId) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = """
                    SELECT 
                        (pto.price_from_start_kinder - pfrom.price_from_start_kinder) AS price
                    FROM TripStopPrice pto
                    JOIN TripStopPrice pfrom ON pto.trip_id = pfrom.trip_id
                    WHERE pto.trip_id = ? AND pfrom.trip_id = ?
                      AND pto.stop_id = ? AND pfrom.stop_id = ?
                """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tripId.toString());
            stmt.setString(2, tripId.toString());
            stmt.setString(3, toStopId.toString());
            stmt.setString(4, fromStopId.toString());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("price");
            }
        }
        throw new SQLException("Kinderpreis nicht gefunden für Stops: " + fromStopId + " → " + toStopId);
    }
}
