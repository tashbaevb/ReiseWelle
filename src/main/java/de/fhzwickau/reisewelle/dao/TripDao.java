package de.fhzwickau.reisewelle.dao;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import de.fhzwickau.reisewelle.dto.TripDetailsDTO;
import de.fhzwickau.reisewelle.dto.TripSegmentDTO;
import de.fhzwickau.reisewelle.model.SeatAvailability;
import de.fhzwickau.reisewelle.model.Stop;

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

    private final StopDao stopDao = new StopDao();
    private final SeatAvailabilityDao seatAvailabilityDao = new SeatAvailabilityDao();
    private final TripStopPriceDao tripStopPriceDao = new TripStopPriceDao();

    public List<TripSegmentDTO> searchTrips(String fromCity, String toCity, LocalDateTime dateTime, Integer adults, Integer children, Integer bicycles) throws SQLException {
        List<TripSegmentDTO> segments = new ArrayList<>();
        int totalPassengers = adults + children;

        Connection connection = JDBCConfig.getInstance();

        String sql = """
                SELECT t.id AS trip_id
                FROM Trip t
                JOIN Stop ss ON ss.trip_id = t.id
                JOIN City cs ON ss.city_id = cs.id
                JOIN Stop se ON se.trip_id = t.id
                JOIN City ce ON se.city_id = ce.id
                WHERE cs.name = ? AND ce.name = ? AND ss.departure_time >= ? AND ss.stop_order < se.stop_order
                GROUP BY t.id, ss.id, se.id
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fromCity);
            stmt.setString(2, toCity);
            stmt.setTimestamp(3, Timestamp.valueOf(dateTime));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UUID tripId = UUID.fromString(rs.getString("trip_id"));

                List<Stop> stops = stopDao.findAllByTripOrdered(tripId);

                Stop fromStop = null;
                Stop toStop = null;
                for (Stop s : stops) {
                    if (s.getCity().getName().equals(fromCity)) fromStop = s;
                    if (s.getCity().getName().equals(toCity)) toStop = s;
                }

                if (fromStop == null || toStop == null) continue;

                int fromIndex = -1, toIndex = -1;
                for (int i = 0; i < stops.size(); i++) {
                    if (stops.get(i).getId().equals(fromStop.getId())) fromIndex = i;
                    if (stops.get(i).getId().equals(toStop.getId())) toIndex = i;
                }

                if (fromIndex == -1 || toIndex == -1 || fromIndex >= toIndex) continue;

                boolean hasSeats = true;

                for (int i = fromIndex; i < toIndex; i++) {
                    Stop segStart = stops.get(i);
                    Stop segEnd = stops.get(i + 1);

                    SeatAvailability sa = seatAvailabilityDao.findByTripAndStops(tripId, segStart.getId(), segEnd.getId());

                    if (sa == null || sa.getAvailableSeats() < totalPassengers || sa.getAvailableBicycleSeats() < bicycles) {
                        hasSeats = false;
                        break;
                    }
                }

                if (!hasSeats) continue;

                double priceAdult = tripStopPriceDao.getPriceAdult(tripId, fromStop.getId(), toStop.getId());
                double priceChild = tripStopPriceDao.getPriceChild(tripId, fromStop.getId(), toStop.getId());
                double totalPrice = priceAdult * adults + priceChild * children;

                segments.add(new TripSegmentDTO(
                        tripId,
                        fromCity,
                        toCity,
                        fromStop.getDepartureTime(),
                        toStop.getArrivalTime(),
                        totalPrice,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        fromStop.getId(),
                        toStop.getId()
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
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

