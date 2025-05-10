package de.fhzwickau.reisewelle.repository;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.model.TripDetailsDTO;
import de.fhzwickau.reisewelle.model.TripSegmentDTO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TripRepository {

    public List<TripSegmentDTO> searchTrips(String fromCity, String toCity, LocalDate date, int adults, int children, int bicycles) {
        List<TripSegmentDTO> segments = new ArrayList<>();

        String sql = """
                SELECT t.id as trip_id,
                       ss.departure_time AS departure_time,
                       se.arrival_time AS arrival_time,
                       ss.stop_order AS from_order,
                       se.stop_order AS to_order,
                       cs.name AS from_city,
                       ce.name AS to_city,
                       (
                           SELECT MIN(sa.available_seats)
                           FROM SeatAvailability sa
                           JOIN Stop s_start ON sa.start_stop_id = s_start.id
                           JOIN Stop s_end ON sa.end_stop_id = s_end.id
                           WHERE sa.trip_id = t.id
                             AND s_start.stop_order >= ss.stop_order
                             AND s_end.stop_order <= se.stop_order
                       ) AS min_available,
                       (
                           SELECT SUM(sp.price_from_start)  -- Corrected to 'price_from_start'
                           FROM TripStopPrice sp           -- Corrected to join 'TripStopPrice'
                           WHERE sp.trip_id = t.id
                             AND sp.stop_id = ss.id         -- Ensure proper stop_id match
                             AND sp.stop_id = se.id         -- Adjust for stop_id range or use specific logic
                       ) AS total_price
                FROM Trip t
                JOIN Stop ss ON ss.trip_id = t.id
                JOIN City cs ON ss.city_id = cs.id
                JOIN Stop se ON se.trip_id = t.id
                JOIN City ce ON se.city_id = ce.id
                WHERE cs.name = ?
                  AND ce.name = ?
                  AND t.departure_date = ?
                  AND ss.stop_order < se.stop_order
                """;

        int totalPassengers = adults + children + bicycles;

        try (Connection conn = JDBCConfig.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fromCity);
            stmt.setString(2, toCity);
            stmt.setDate(3, Date.valueOf(date));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int availableSeats = rs.getInt("min_available");
                if (availableSeats >= totalPassengers) {
                    UUID tripId = UUID.fromString(rs.getString("trip_id"));
                    String from = rs.getString("from_city");
                    String to = rs.getString("to_city");
                    LocalDateTime departure = rs.getTimestamp("departure_time").toLocalDateTime();
                    LocalDateTime arrival = rs.getTimestamp("arrival_time").toLocalDateTime();
                    double price = rs.getDouble("total_price");

                    TripSegmentDTO dto = new TripSegmentDTO(tripId, from, to, departure, arrival, price, availableSeats);
                    segments.add(dto);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // желательно логировать
        }

        return segments;
    }



    public TripDetailsDTO getTripDetails(UUID tripId) {
        String sql = """
            SELECT 
                d.first_name,
                d.last_name,
                b.bus_number,
                sa.available_seats,
                c.name AS city_name,
                s.departure_time,
                s.stop_order
            FROM Trip t
            JOIN Driver d ON t.driver_id = d.id
            JOIN Bus b ON t.bus_id = b.id
            LEFT JOIN SeatAvailability sa ON sa.trip_id = t.id
            JOIN Stop s ON s.trip_id = t.id
            JOIN City c ON s.city_id = c.id
            WHERE t.id = ?
            ORDER BY s.stop_order
        """;

        try (Connection conn = JDBCConfig.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, tripId);
            ResultSet rs = stmt.executeQuery();

            String driverName = null;
            String busNumber = null;
            int availableSeats = 0;
            List<String> stops = new ArrayList<>();

            while (rs.next()) {
                if (driverName == null) {
                    driverName = rs.getString("first_name") + " " + rs.getString("last_name");
                    busNumber = rs.getString("bus_number");
                    availableSeats = rs.getInt("available_seats");
                }

                String city = rs.getString("city_name");
                Timestamp departure = rs.getTimestamp("departure_time");
                String timeString = departure != null ? departure.toLocalDateTime().toLocalTime().toString() : "время неизвестно";
                stops.add(city + " - " + timeString);
            }

            if (driverName != null) {
                return new TripDetailsDTO(driverName, busNumber, availableSeats, stops);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}

