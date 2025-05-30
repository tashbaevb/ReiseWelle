package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.Stop;
import de.fhzwickau.reisewelle.model.Ticket;
import de.fhzwickau.reisewelle.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TicketDao implements BaseDao<Ticket> {

    private final BaseDao<User> userDao = new UserDao();
    private final BaseDao<Stop> stopDao = new StopDao();

    @Override
    public List<Ticket> findAll() throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        List<Ticket> list = new ArrayList<>();

        String sql = """
                    SELECT id, user_id, trip_id, start_stop_id, end_stop_id, adult_count,
                        child_count, bike_count, total_price, purchase_date 
                    FROM Ticket
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public Ticket findById(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        String sql = """
                    SELECT user_id, trip_id, start_stop_id, end_stop_id,
                        adult_count, child_count, bike_count, total_price, purchase_date
                    FROM Ticket WHERE id = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Ticket t = mapRow(rs);
                    t.setId(id);
                    return t;
                }
            }
        }
        return null;
    }

    @Override
    public void save(Ticket ticket) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        boolean isNew = ticket.getId() == null;
        String sql = isNew
                ? "INSERT INTO Ticket (id, user_id, trip_id, start_stop_id, end_stop_id, adult_count, child_count, bike_count, total_price, purchase_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                : "UPDATE Ticket SET user_id=?, trip_id=?, start_stop_id=?, end_stop_id=?, adult_count=?, child_count=?, bike_count=?, total_price=?, purchase_date=? WHERE id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int i = 1;
            if (isNew) {
                ticket.setId(UUID.randomUUID());
                stmt.setString(i++, ticket.getId().toString());
            }
            stmt.setString(i++, ticket.getUser().getId().toString());
            stmt.setString(i++, ticket.getTrip().getId().toString());
            stmt.setString(i++, ticket.getStartStop().getId().toString());
            stmt.setString(i++, ticket.getEndStop().getId().toString());
            stmt.setInt(i++, ticket.getAdultCount());
            stmt.setInt(i++, ticket.getChildCount());
            stmt.setInt(i++, ticket.getBikeCount());
            stmt.setDouble(i++, ticket.getTotalPrice());
            stmt.setTimestamp(i++, Timestamp.valueOf(ticket.getPurchaseDate()));
            if (!isNew) stmt.setString(i, ticket.getId().toString());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(UUID id) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Ticket WHERE id = ?")) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        }
    }

    public void deleteAllForTrip(UUID tripId) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Ticket WHERE trip_id = ?")) {
            stmt.setString(1, tripId.toString());
            stmt.executeUpdate();
        }
    }

    public void deleteAllForStop(UUID stopId) throws SQLException {
        Connection conn = JDBCConfig.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Ticket WHERE start_stop_id = ? OR end_stop_id = ?")) {
            stmt.setString(1, stopId.toString());
            stmt.setString(2, stopId.toString());
            stmt.executeUpdate();
        }
    }

    private Ticket mapRow(ResultSet rs) throws SQLException {
        UUID userId = UUID.fromString(rs.getString("user_id"));
        UUID tripId = UUID.fromString(rs.getString("trip_id"));
        UUID startId = UUID.fromString(rs.getString("start_stop_id"));
        UUID endId = UUID.fromString(rs.getString("end_stop_id"));

        TripAdminDao tripDao = new TripAdminDao();

        return new Ticket(
                userDao.findById(userId),
                tripDao.findById(tripId),
                stopDao.findById(startId),
                stopDao.findById(endId),
                rs.getInt("adult_count"),
                rs.getInt("child_count"),
                rs.getInt("bike_count"),
                rs.getDouble("total_price"),
                rs.getTimestamp("purchase_date").toLocalDateTime()
        );
    }
}
