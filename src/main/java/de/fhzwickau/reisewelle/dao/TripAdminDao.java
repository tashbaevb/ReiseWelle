// src/main/java/de/fhzwickau/reisewelle/dao/TripAdminDao.java
package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.Trip;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TripAdminDao implements BaseDao<Trip> {

    private final BusDao busDao               = new BusDao();
    private final DriverDao driverDao         = new DriverDao();
    private final TripStatusDao statusDao     = new TripStatusDao();
    private final TripStopPriceDao priceDao   = new TripStopPriceDao();
    private final SeatAvailabilityDao seatDao = new SeatAvailabilityDao();
    private final StopDao stopDao             = new StopDao();

    @Override
    public List<Trip> findAll() throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, bus_id, driver_id, departure_date, status_id FROM Trip";
        List<Trip> list = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public Trip findById(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = "SELECT id, bus_id, driver_id, departure_date, status_id FROM Trip WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public void save(Trip trip) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        boolean isNew = trip.getId() == null;
        String sql = isNew
                ? "INSERT INTO Trip (id, bus_id, driver_id, departure_date, status_id) VALUES (?, ?, ?, ?, ?)"
                : "UPDATE Trip SET bus_id = ?, driver_id = ?, departure_date = ?, status_id = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (isNew) {
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
        }
    }

    @Override
    public void delete(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        // 1) цены
        priceDao.deleteAllForTrip(id);
        // 2) билеты
        new TicketDao().deleteAllForTrip(id);
        // 3) доступность мест
        seatDao.deleteAllForTrip(id);
        // 4) остановки
        stopDao.deleteAllForTrip(id);
        // 5) сам рейс
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM Trip WHERE id = ?"
        )) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        }
    }

    private Trip mapRow(ResultSet rs) throws SQLException {
        UUID id       = UUID.fromString(rs.getString("id"));
        UUID busId    = UUID.fromString(rs.getString("bus_id"));
        UUID drvId    = UUID.fromString(rs.getString("driver_id"));
        UUID stId     = UUID.fromString(rs.getString("status_id"));
        Trip trip = new Trip(
                busDao.findById(busId),
                driverDao.findById(drvId),
                rs.getDate("departure_date").toLocalDate(),
                statusDao.findById(stId)
        );
        trip.setId(id);
        return trip;
    }
}
