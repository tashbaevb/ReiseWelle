package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.Bus;
import de.fhzwickau.reisewelle.model.Driver;
import de.fhzwickau.reisewelle.model.Trip;
import de.fhzwickau.reisewelle.model.TripStatus;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TripAdminRepository {
    private final BusRepository busRepository = new BusRepository();
    private final DriverRepository driverRepository = new DriverRepository();
    private final TripStatusRepository tripStatusRepository = new TripStatusRepository();

    public List<Trip> findAll() {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT id, bus_id, driver_id, departure_date, status_id FROM Trip";

        try (Connection conn = JDBCConfig.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                UUID busId = UUID.fromString(rs.getString("bus_id"));
                UUID driverId = UUID.fromString(rs.getString("driver_id"));
                UUID statusId = UUID.fromString(rs.getString("status_id"));
                Bus bus = busRepository.findById(busId);
                Driver driver = driverRepository.findById(driverId);
                TripStatus status = tripStatusRepository.findById(statusId);
                Trip trip = new Trip(
                        bus,
                        driver,
                        rs.getDate("departure_date").toLocalDate(),
                        status
                );
                trip.setId(UUID.fromString(rs.getString("id")));
                trips.add(trip);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trips;
    }

    public Trip findById(UUID id) {
        String sql = "SELECT id, bus_id, driver_id, departure_date, status_id FROM Trip WHERE id = ?";
        try (Connection conn = JDBCConfig.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UUID busId = UUID.fromString(rs.getString("bus_id"));
                    UUID driverId = UUID.fromString(rs.getString("driver_id"));
                    UUID statusId = UUID.fromString(rs.getString("status_id"));
                    Bus bus = busRepository.findById(busId);
                    Driver driver = driverRepository.findById(driverId);
                    TripStatus status = tripStatusRepository.findById(statusId);
                    Trip trip = new Trip(
                            bus,
                            driver,
                            rs.getDate("departure_date").toLocalDate(),
                            status
                    );
                    trip.setId(UUID.fromString(rs.getString("id")));
                    return trip;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(Trip trip) {
        String sql = trip.getId() == null ?
                "INSERT INTO Trip (id, bus_id, driver_id, departure_date, status_id) VALUES (?, ?, ?, ?, ?)" :
                "UPDATE Trip SET bus_id = ?, driver_id = ?, departure_date = ?, status_id = ? WHERE id = ?";

        try (Connection conn = JDBCConfig.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (trip.getId() == null) {
                trip.setId(UUID.randomUUID());
                stmt.setString(1, trip.getId().toString());
                stmt.setString(2, trip.getBus().getId().toString());
                stmt.setString(3, trip.getDriver().getId().toString());
                stmt.setDate(4, Date.valueOf(trip.getDepartureDate()));
                stmt.setString(5, trip.getStatus().getId().toString());
            } else {
                stmt.setString(1, trip.getBus().getId().toString());
                stmt.setString(2, trip.getDriver().getId().toString());
                stmt.setDate(3, Date.valueOf(trip.getDepartureDate()));
                stmt.setString(4, trip.getStatus().getId().toString());
                stmt.setString(5, trip.getId().toString());
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(UUID id) {
        String sql = "DELETE FROM Trip WHERE id = ?";
        try (Connection conn = JDBCConfig.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}