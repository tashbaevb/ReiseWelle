package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.Bus;
import de.fhzwickau.reisewelle.model.Status;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BusDao implements BaseDao<Bus> {

    private final BaseDao<Status> statusDao = new StatusDao();

    public List<Bus> findAll() throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = """
                SELECT b.id, b.bus_number, b.total_seats,
                       b.total_bicycle_spaces,
                       s.id AS status_id, s.name AS status_name 
                FROM Bus b
                JOIN Status s ON b.status_id = s.id
                """;
        List<Bus> buses = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                buses.add(mapBus(rs));
            }
        }
        return buses;
    }

    public Bus findById(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = """
                SELECT b.id, b.bus_number, b.total_seats,
                       b.total_bicycle_spaces,
                       s.id AS status_id, s.name AS status_name 
                FROM Bus b
                JOIN Status s ON b.status_id = s.id
                WHERE b.id = ?
                """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapBus(rs);
                }
            }
        }
        return null;
    }

    public boolean isBusWithStatusId(UUID id) throws SQLException {
        Connection connection = JDBCConfig.getInstance();
        String sql = "SELECT TOP 1 id FROM Bus WHERE status_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id.toString());

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void save(Bus bus) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        boolean isNew = bus.getId() == null;

        String sql = isNew
                ? "INSERT INTO Bus (id, bus_number, total_seats, status_id,  total_bicycle_spaces) VALUES (?, ?, ?, ?, ?)"
                : "UPDATE Bus SET bus_number = ?, total_seats = ?, status_id = ?, total_bicycle_spaces = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (isNew) {
                bus.setId(UUID.randomUUID());
                stmt.setString(1, bus.getId().toString());
                stmt.setString(2, bus.getBusNumber());
                stmt.setInt(3, bus.getTotalSeats());
                stmt.setString(4, bus.getStatus().getId().toString());
                stmt.setInt(5, bus.getBicycleSpaces());
            } else {
                stmt.setString(1, bus.getBusNumber());
                stmt.setInt(2, bus.getTotalSeats());
                stmt.setString(3, bus.getStatus().getId().toString());
                stmt.setInt(4, bus.getBicycleSpaces());
                stmt.setString(5, bus.getId().toString());
            }
            stmt.executeUpdate();
        }
    }

    public void delete(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Bus WHERE id = ?")) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        }
    }

    private Bus mapBus(ResultSet rs) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        String busNumber = rs.getString("bus_number");
        int totalSeats = rs.getInt("total_seats");
        int bikeSpaces = rs.getInt("total_bicycle_spaces");
        UUID statusId = UUID.fromString(rs.getString("status_id"));
        Status status = statusDao.findById(statusId);

        return new Bus(id, busNumber, totalSeats, status, bikeSpaces);
    }
}
