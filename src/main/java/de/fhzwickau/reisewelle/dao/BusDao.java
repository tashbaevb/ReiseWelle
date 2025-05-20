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

public class BusDao {

    private final StatusDao statusDao = new StatusDao();

    public List<Bus> findAll() throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        List<Bus> buses = new ArrayList<>();

        String sql = "SELECT id, bus_number, total_seats, status_id FROM Bus";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                UUID statusId = UUID.fromString(rs.getString("status_id"));
                Status status = statusDao.findById(statusId);
                Bus bus = new Bus(
                        rs.getString("bus_number"),
                        rs.getInt("total_seats"),
                        status
                );
                bus.setId(UUID.fromString(rs.getString("id")));
                buses.add(bus);
            }
            System.out.println("Loaded buses: " + buses.size());
        } catch (SQLException e) {
            System.err.println("Error in findAll: " + e.getMessage());
            e.printStackTrace();
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
                    UUID statusId = UUID.fromString(rs.getString("status_id"));
                    Status status = statusDao.findById(statusId);
                    Bus bus = new Bus(
                            rs.getString("bus_number"),
                            rs.getInt("total_seats"),
                            status
                    );
                    bus.setId(UUID.fromString(rs.getString("id")));
                    return bus;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in findById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void save(Bus bus) throws SQLException {
        Connection conn = JDBCConfig.getInstance();

        String sql = bus.getId() == null ?
                "INSERT INTO Bus (id, bus_number, total_seats, status_id) VALUES (?, ?, ?, ?)" :
                "UPDATE Bus SET bus_number = ?, total_seats = ?, status_id = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (bus.getId() == null) {
                bus.setId(UUID.randomUUID()); // Генерируем новый UUID для новой записи
                System.out.println("Generating new ID: " + bus.getId());
                stmt.setString(1, bus.getId().toString());
                stmt.setString(2, bus.getBusNumber());
                stmt.setInt(3, bus.getTotalSeats());
                stmt.setString(4, bus.getStatus().getId().toString());
                System.out.println("Executing INSERT: id=" + bus.getId() + ", number=" + bus.getBusNumber() + ", seats=" + bus.getTotalSeats() + ", statusId=" + bus.getStatus().getId());
            } else {
                stmt.setString(1, bus.getBusNumber());
                stmt.setInt(2, bus.getTotalSeats());
                stmt.setString(3, bus.getStatus().getId().toString());
                stmt.setString(4, bus.getId().toString());
                System.out.println("Executing UPDATE: id=" + bus.getId() + ", number=" + bus.getBusNumber() + ", seats=" + bus.getTotalSeats() + ", statusId=" + bus.getStatus().getId());
            }
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Error in save: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void delete(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();

        String sql = "DELETE FROM Bus WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}