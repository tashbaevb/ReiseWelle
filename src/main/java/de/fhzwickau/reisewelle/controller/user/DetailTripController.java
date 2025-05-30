package de.fhzwickau.reisewelle.controller.user;

import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.SeatAvailabilityDao;
import de.fhzwickau.reisewelle.dao.TicketDao;
import de.fhzwickau.reisewelle.dao.UserDao;
import de.fhzwickau.reisewelle.model.SeatAvailability;
import de.fhzwickau.reisewelle.model.Ticket;
import de.fhzwickau.reisewelle.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import de.fhzwickau.reisewelle.dto.TripDetailsDTO;
import de.fhzwickau.reisewelle.dao.TripDao;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class DetailTripController {

    private final BaseDao<SeatAvailability> seatDao = new SeatAvailabilityDao();
    private final BaseDao<Ticket> ticketDao = new TicketDao();
    private final BaseDao<User> userDao = new UserDao();

    private UUID tripId;
    private String startStopId, endStopId;
    private int adults, children, bikes;
    private double price;

    @FXML
    private Label busLabel, priceLabel;
    @FXML
    private ListView<String> stopsList;

    private final TripDao tripDao = new TripDao();

    public void loadTripDetails(UUID tripId, Double price, String startStopId, String endStopId, int adults, int children, int bikes) throws SQLException {
        this.tripId = tripId;
        this.startStopId = startStopId;
        this.endStopId = endStopId;
        this.adults = adults;
        this.children = children;
        this.bikes = bikes;
        this.price = price;

        TripDetailsDTO trip = tripDao.getTripDetails(tripId, price);
        if (trip == null) {
            busLabel.setText("Keine Detail Informationen");
            throw new IllegalArgumentException("Trip kann nicht geöffnet werden");
        }

        busLabel.setText(trip.getBusNumber());
        priceLabel.setText(String.valueOf(trip.getPrice()));
        stopsList.getItems().setAll(trip.getStops());
    }


    public void onBuy(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Ticket kaufen?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            updateTripInfo();
        }
    }

    private void updateTripInfo() {
        try {
            UUID userId = UUID.fromString("444494BA-8390-468D-B157-F586B8AE3F80");
            User user = userDao.findById(userId);
            System.out.println("userchik = " + user.getId());

            UUID startUUID = UUID.fromString(startStopId);
            UUID endUUID = UUID.fromString(endStopId);

            SeatAvailability availability = seatDao.findAll().stream()
                    .filter(sa -> sa.getTrip().getId().equals(tripId)
                            && sa.getStartStop().getId().equals(startUUID)
                            && sa.getEndStop().getId().equals(endUUID))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Keine Verfügbarkeit gefunden"));

            int totalPassengers = adults + children;

            if (availability.getAvailableSeats() < (adults + children)) {
                showError("Nicht genug freie Sitzplätze.");
                return;
            }
            if (availability.getAvailableBicycleSeats() < bikes) {
                showError("Nicht genug Fahrradplätze.");
                return;
            }

            availability.setAvailableSeats(availability.getAvailableSeats() - totalPassengers);
            availability.setAvailableBicycleSeats(availability.getAvailableBicycleSeats() - bikes);
            seatDao.save(availability);

            Ticket ticket = new Ticket(
                    user,
                    availability.getTrip(),
                    availability.getStartStop(),
                    availability.getEndStop(),
                    adults,
                    children,
                    bikes,
                    price,
                    LocalDateTime.now()
            );
            ticketDao.save(ticket);

            showSuccess("Ticket wurde erfolgreich gekauft!");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Fehler beim Kauf: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Fehler");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showSuccess(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Erfolg");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
