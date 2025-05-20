package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.dto.TripDetailsDTO;
import de.fhzwickau.reisewelle.dto.TripSegmentDTO;

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

    private Connection conn = null;

    public TripRepository() throws SQLException {
        conn = JDBCConfig.getInstance();
    }

    public List<TripSegmentDTO> searchTrips(String fromCity, String toCity, LocalDateTime dateTime, int adults, int children, int bicycles) {
        List<TripSegmentDTO> segments = new ArrayList<>();
        String sql = """
        SELECT t.id as trip_id,
               ss.id as start_id,
               se.id as end_id,
               ss.departure_time AS departure_time,
               se.arrival_time AS arrival_time,
               cs.name AS from_city,
               ce.name AS to_city,
               (
                   SELECT MIN(sa.available_seats)
                   FROM SeatAvailability sa
                   WHERE sa.trip_id = t.id
                     AND sa.start_stop_id = ss.id
                     AND sa.end_stop_id = se.id
               ) AS min_available,
               (
                   SELECT MIN(sa.available_bicycle_seats)
                   FROM SeatAvailability sa
                   WHERE sa.trip_id = t.id
                     AND sa.start_stop_id = ss.id
                     AND sa.end_stop_id = se.id
               ) AS min_bikes,
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
               ) AS price_child
        FROM Trip t
        JOIN Stop ss ON ss.trip_id = t.id
        JOIN City cs ON ss.city_id = cs.id
        JOIN Stop se ON se.trip_id = t.id
        JOIN City ce ON se.city_id = ce.id
        WHERE cs.name = ?
          AND ce.name = ?
          AND t.departure_date = ?
          AND ss.departure_time >= ?
          AND ss.stop_order < se.stop_order
    """;

        int totalPassengers = adults + children;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fromCity);
            stmt.setString(2, toCity);
            stmt.setDate(3, Date.valueOf(dateTime.toLocalDate()));
            stmt.setTimestamp(4, Timestamp.valueOf(dateTime));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int seats = rs.getInt("min_available");
                int bikes = rs.getInt("min_bikes");

                if (seats >= totalPassengers && bikes >= bicycles) {
                    UUID tripId = UUID.fromString(rs.getString("trip_id"));
                    String from = rs.getString("from_city");
                    String to = rs.getString("to_city");
                    LocalDateTime departure = rs.getTimestamp("departure_time").toLocalDateTime();
                    LocalDateTime arrival = rs.getTimestamp("arrival_time").toLocalDateTime();

                    double priceAdult = rs.getDouble("price_adult");
                    boolean isAdultNull = rs.wasNull();

                    double priceChild = rs.getDouble("price_child");
                    boolean isChildNull = rs.wasNull();

                    if (!isAdultNull && !isChildNull) {
                        System.out.println("CHILD = " + priceChild);
                        System.out.println("ADULT = " + priceAdult);
                        double price = priceAdult * adults + priceChild * children;

                        TripSegmentDTO dto = new TripSegmentDTO(tripId, from, to, departure, arrival, price, seats, bikes);
                        segments.add(dto);
                    }

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
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

