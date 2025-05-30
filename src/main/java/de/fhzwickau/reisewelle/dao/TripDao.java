package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.dto.TripDetailsDTO;
import de.fhzwickau.reisewelle.dto.TripSegmentDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TripDao {

    public List<TripSegmentDTO> searchTrips(String fromCity, String toCity, LocalDateTime dateTime, Integer adults, Integer children, Integer bicycles) throws SQLException {
        Connection connection = JDBCConfig.getInstance();
        List<TripSegmentDTO> segments = new ArrayList<>();

        String sql = """
                    SELECT
                    t.id as trip_id,
                    ss.id as start_id,
                    se.id as end_id,
                    ss.departure_time AS departure_time,
                    se.arrival_time AS arrival_time,
                    cs.name AS from_city,
                    ce.name AS to_city,
                    (
                        SELECT pto.price_from_start_adult - pfrom.price_from_start_adult
                        FROM TripStopPrice pto
                        JOIN TripStopPrice pfrom ON pto.trip_id = pfrom.trip_id
                        WHERE pto.stop_id = se.id AND pfrom.stop_id = ss.id AND pto.trip_id = t.id
                    ) AS price_adult,
                    (
                        SELECT pto.price_from_start_kinder - pfrom.price_from_start_kinder
                        FROM TripStopPrice pto
                        JOIN TripStopPrice pfrom ON pto.trip_id = pfrom.trip_id
                        WHERE pto.stop_id = se.id AND pfrom.stop_id = ss.id AND pto.trip_id = t.id
                    ) AS price_child,
                    (
                        SELECT MIN(sa.available_seats)
                        FROM SeatAvailability sa
                        WHERE sa.trip_id = t.id AND sa.start_stop_id = ss.id AND sa.end_stop_id = se.id
                    ) AS min_available,
                    (
                        SELECT MIN(sa.available_bicycle_spaces)
                        FROM SeatAvailability sa
                        WHERE sa.trip_id = t.id AND sa.start_stop_id = ss.id AND sa.end_stop_id = se.id
                    ) AS min_bikes
                FROM Trip t
                JOIN Stop ss ON ss.trip_id = t.id
                JOIN City cs ON ss.city_id = cs.id
                JOIN Stop se ON se.trip_id = t.id
                JOIN City ce ON se.city_id = ce.id
                WHERE cs.name = ?
                  AND ce.name = ?
                  AND ss.departure_time >= ?
                  AND ss.stop_order < se.stop_order
                  AND (
                        SELECT MIN(sa.available_seats)
                        FROM SeatAvailability sa
                        WHERE sa.trip_id = t.id AND sa.start_stop_id = ss.id AND sa.end_stop_id = se.id
                  ) >= ?
                  AND (
                        SELECT MIN(sa.available_bicycle_spaces)
                        FROM SeatAvailability sa
                        WHERE sa.trip_id = t.id AND sa.start_stop_id = ss.id AND sa.end_stop_id = se.id
                  ) >= ?
                """;

        int totalPassengers = adults + children;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fromCity);
            stmt.setString(2, toCity);
            stmt.setTimestamp(3, Timestamp.valueOf(dateTime));
            stmt.setInt(4, totalPassengers);
            stmt.setInt(5, bicycles);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UUID tripId = UUID.fromString(rs.getString("trip_id"));
                UUID startStopId = UUID.fromString(rs.getString("start_id"));
                UUID endStopId = UUID.fromString(rs.getString("end_id"));

                int seats = rs.getInt("min_available");
                int bikes = rs.getInt("min_bikes");
                String from = rs.getString("from_city");
                String to = rs.getString("to_city");

                LocalDateTime departure = rs.getTimestamp("departure_time").toLocalDateTime();
                LocalDateTime arrival = rs.getTimestamp("arrival_time").toLocalDateTime();

                double priceAdult = rs.getDouble("price_adult");
                boolean isAdultNull = rs.wasNull();

                double priceChild = rs.getDouble("price_child");
                boolean isChildNull = rs.wasNull();

                if (!isAdultNull && !isChildNull) {
                    double price = priceAdult * adults + priceChild * children;
                    TripSegmentDTO dto = new TripSegmentDTO(tripId, from, to, departure, arrival, price, seats, bikes, startStopId, endStopId);
                    segments.add(dto);
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw sqle;
        }

        return segments;
    }

    public TripDetailsDTO getTripDetails(UUID tripId, Double price) throws SQLException {
        Connection connection = JDBCConfig.getInstance();

        String sql = """
                    SELECT DISTINCT 
                        b.bus_number,
                        c.name AS city_name,
                        s.departure_time,
                        s.stop_order
                    FROM Trip t
                    JOIN Bus b ON t.bus_id = b.id
                    JOIN Stop s ON s.trip_id = t.id
                    JOIN City c ON s.city_id = c.id
                    WHERE t.id = ?
                    ORDER BY s.stop_order
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, tripId);
            ResultSet rs = stmt.executeQuery();

            String busNumber = null;
            List<String> stops = new ArrayList<>();

            while (rs.next()) {
                if (busNumber == null) {
                    busNumber = rs.getString("bus_number");
                }

                String city = rs.getString("city_name");
                Timestamp departure = rs.getTimestamp("departure_time");
                String timeString = departure != null ? departure.toLocalDateTime().toLocalTime().toString() : "Ende";
                stops.add(city + " - " + timeString);
            }

            if (busNumber != null) {
                return new TripDetailsDTO(busNumber, stops, price);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}

