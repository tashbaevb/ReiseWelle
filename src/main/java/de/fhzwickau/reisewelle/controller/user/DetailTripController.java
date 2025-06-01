package de.fhzwickau.reisewelle.controller.user;

import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.SeatAvailabilityDao;
import de.fhzwickau.reisewelle.dao.StopDao;
import de.fhzwickau.reisewelle.dao.TicketDao;
import de.fhzwickau.reisewelle.dao.TripAdminDao;
import de.fhzwickau.reisewelle.dao.UserDao;
import de.fhzwickau.reisewelle.model.SeatAvailability;
import de.fhzwickau.reisewelle.model.Stop;
import de.fhzwickau.reisewelle.model.Ticket;
import de.fhzwickau.reisewelle.model.User;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import de.fhzwickau.reisewelle.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import de.fhzwickau.reisewelle.dto.TripDetailsDTO;
import de.fhzwickau.reisewelle.dao.TripDao;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DetailTripController {

    private final SeatAvailabilityDao seatDao = new SeatAvailabilityDao();
    private final BaseDao<Ticket> ticketDao = new TicketDao();
    private final BaseDao<User> userDao = new UserDao();
    private final StopDao stopDao = new StopDao();

    private UUID tripId;
    private String startStopId, endStopId;
    private int adults, children, bikes;
    private double price;

    @FXML
    private Label busLabel, priceLabel;
    @FXML
    private ListView<String> stopsList;
    @FXML
    private Button buyBtn;

    private final TripDao tripDao = new TripDao();
    private final TripAdminDao tripAdminDao = new TripAdminDao();
    private boolean isViewingTicket = false;

    public void setViewingTicket(boolean viewingTicket) {
        this.isViewingTicket = viewingTicket;
    }

    public void loadTripDetails(UUID tripId, Double price, String startStopId, String endStopId, int adults, int children, int bikes) throws SQLException {
        this.tripId = tripId;
        this.startStopId = startStopId;
        this.endStopId = endStopId;
        this.adults = adults;
        this.children = children;
        this.bikes = bikes;
        this.price = price;
        buyBtn.setVisible(!isViewingTicket);

        TripDetailsDTO trip = tripDao.getTripDetails(tripId, price);
        if (trip == null) {
            busLabel.setText("Keine Detail Informationen");
            throw new IllegalArgumentException("Trip kann nicht geöffnet werden");
        }

        busLabel.setText(trip.getBusNumber());
        priceLabel.setText(String.valueOf(trip.getPrice()));
        stopsList.getItems().setAll(trip.getStops());
    }

    public void onBuy() {
        AlertUtil.showConfirmation("Ticket kaufen?", confirmed -> {
            if (confirmed) {
                updateTripInfo();
            }
        });
    }

    private void updateTripInfo() {
        try {
            UUID userId = Session.getInstance().getCurrentUser().getId();
            User user = userDao.findById(userId);

            UUID startUUID = UUID.fromString(startStopId);
            UUID endUUID = UUID.fromString(endStopId);

            int totalPassengers = adults + children;

            List<Stop> allStops = stopDao.findAllByTripOrdered(tripId);

            int fromIndex = -1, toIndex = -1;
            for (int i = 0; i < allStops.size(); i++) {
                if (allStops.get(i).getId().equals(startUUID)) fromIndex = i;
                if (allStops.get(i).getId().equals(endUUID)) toIndex = i;
            }

            if (fromIndex == -1 || toIndex == -1 || fromIndex >= toIndex) {
                AlertUtil.showError("Fehler", "Ungültige Stopps gewählt");
                return;
            }

            // Check availability of seats on all segments
            for (int i = fromIndex; i < toIndex; i++) {
                UUID segStartId = allStops.get(i).getId();
                UUID segEndId = allStops.get(i + 1).getId();
                SeatAvailability sa = seatDao.findByTripAndStops(tripId, segStartId, segEndId);

                if (sa == null) {
                    AlertUtil.showError("Fehler", "Keine Sitzplatzdaten für Segment " + (i + 1));
                    return;
                }

                if (sa.getAvailableSeats() < totalPassengers) {
                    AlertUtil.showError("Fehler", "Nicht genug freie Sitzplätze auf Teilstrecke.");
                    return;
                }

                if (sa.getAvailableBicycleSeats() < bikes) {
                    AlertUtil.showError("Fehler", "Nicht genug Fahrradplätze auf Teilstrecke.");
                    return;
                }
            }

            // update the availability of seats on all segments
            for (int i = fromIndex; i < toIndex; i++) {
                UUID segStartId = allStops.get(i).getId();
                UUID segEndId = allStops.get(i + 1).getId();
                SeatAvailability sa = seatDao.findByTripAndStops(tripId, segStartId, segEndId);

                if (sa == null) {
                    AlertUtil.showError("Fehler", "Fehlende Sitzverfügbarkeitsdaten bei Aktualisierung.");
                    return;
                }

                sa.setAvailableSeats(sa.getAvailableSeats() - totalPassengers);
                sa.setAvailableBicycleSeats(sa.getAvailableBicycleSeats() - bikes);
                seatDao.save(sa);
            }

            Ticket ticket = new Ticket(
                    user,
                    tripAdminDao.findById(tripId),
                    stopDao.findById(startUUID),
                    stopDao.findById(endUUID),
                    adults,
                    children,
                    bikes,
                    price,
                    LocalDateTime.now()
            );
            ticketDao.save(ticket);

            AlertUtil.showInfo("Erfolgreich!", "Ticket wurde erfolgreich gekauft!");
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Fehler", "Fehler beim Kauf: " + e.getMessage());
        }
    }
}
