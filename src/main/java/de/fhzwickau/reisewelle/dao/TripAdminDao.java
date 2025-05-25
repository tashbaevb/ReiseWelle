package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.*;
import de.fhzwickau.reisewelle.model.Driver;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TripAdminDao implements BaseDao<Trip> {

    private final BusDao busDao = new BusDao();
    private final DriverDao driverDao = new DriverDao();
    private final TripStatusDao tripStatusDao = new TripStatusDao();

    public List<Trip> findAll() throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, bus_id, driver_id, departure_date, status_id FROM Trip";
        List<Trip> trips = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                trips.add(mapRowToTrip(rs));
            }
        }

        return trips;
    }

    public Trip findById(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, bus_id, driver_id, departure_date, status_id FROM Trip WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToTrip(rs);
                }
            }
        }

        return null;
    }

    public void save(Trip trip) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        boolean isNew = trip.getId() == null;

        String sql = isNew
                ? "INSERT INTO Trip (id, bus_id, driver_id, departure_date, status_id) VALUES (?, ?, ?, ?, ?)"
                : "UPDATE Trip SET bus_id = ?, driver_id = ?, departure_date = ?, status_id = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (isNew) {
                trip.setId(UUID.randomUUID());
                prepareInsert(stmt, trip);
            } else {
                prepareUpdate(stmt, trip);
            }
            stmt.executeUpdate();
        }
    }

    public void delete(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "DELETE FROM Trip WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        }
    }

    private Trip mapRowToTrip(ResultSet rs) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        UUID busId = UUID.fromString(rs.getString("bus_id"));
        UUID driverId = UUID.fromString(rs.getString("driver_id"));
        UUID statusId = UUID.fromString(rs.getString("status_id"));

        Bus bus = busDao.findById(busId);
        Driver driver = driverDao.findById(driverId);
        TripStatus status = tripStatusDao.findById(statusId);

        Trip trip = new Trip(bus, driver, rs.getDate("departure_date").toLocalDate(), status);
        trip.setId(id);
        return trip;
    }

    private void prepareInsert(PreparedStatement stmt, Trip trip) throws SQLException {
        stmt.setString(1, trip.getId().toString());
        stmt.setString(2, trip.getBus().getId().toString());
        stmt.setString(3, trip.getDriver().getId().toString());
        stmt.setDate(4, Date.valueOf(trip.getDepartureDate()));
        stmt.setString(5, trip.getStatus().getId().toString());
    }

    private void prepareUpdate(PreparedStatement stmt, Trip trip) throws SQLException {
        stmt.setString(1, trip.getBus().getId().toString());
        stmt.setString(2, trip.getDriver().getId().toString());
        stmt.setDate(3, Date.valueOf(trip.getDepartureDate()));
        stmt.setString(4, trip.getStatus().getId().toString());
        stmt.setString(5, trip.getId().toString());
    }
}