package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.Bus;
import de.fhzwickau.reisewelle.model.Status;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BusDao implements BaseDao<Bus> {

    private final BaseDao<Status> statusDao = new StatusDao();

    public List<Bus> findAll() throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = """
                SELECT b.id, b.bus_number, b.total_seats, s.id AS status_id, s.name AS status_name 
                FROM Bus b JOIN Status s ON b.status_id = s.id
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
        String sql = "SELECT id, bus_number, total_seats, status_id FROM Bus WHERE id = ?";

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

    public void save(Bus bus) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        boolean isNew = bus.getId() == null;
        String sql = isNew
                ? "INSERT INTO Bus (id, bus_number, total_seats, status_id) VALUES (?, ?, ?, ?)"
                : "UPDATE Bus SET bus_number = ?, total_seats = ?, status_id = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (isNew) {
                bus.setId(UUID.randomUUID());
                prepareInsert(stmt, bus);
            } else {
                prepareUpdate(stmt, bus);
            }
            stmt.executeUpdate();
        }
    }

    public void delete(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "DELETE FROM Bus WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        }
    }

    private Bus mapBus(ResultSet rs) throws SQLException {
        UUID statusId = UUID.fromString(rs.getString("status_id"));
        Status status = statusDao.findById(statusId);

        UUID id = UUID.fromString(rs.getString("id"));
        String busNumber = rs.getString("bus_number");
        int totalSeats = rs.getInt("total_seats");

        Bus bus = new Bus(busNumber, totalSeats, status);
        bus.setId(id);
        return bus;
    }

    private void prepareInsert(PreparedStatement stmt, Bus bus) throws SQLException {
        stmt.setObject(1, bus.getId().toString());
        stmt.setString(2, bus.getBusNumber());
        stmt.setInt(3, bus.getTotalSeats());
        stmt.setObject(4, bus.getStatus().getId().toString());
    }

    private void prepareUpdate(PreparedStatement stmt, Bus bus) throws SQLException {
        stmt.setString(1, bus.getBusNumber());
        stmt.setInt(2, bus.getTotalSeats());
        stmt.setObject(3, bus.getStatus().getId().toString());
        stmt.setObject(4, bus.getId().toString());
    }
}