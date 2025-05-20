package de.fhzwickau.reisewelle.controller.user;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import de.fhzwickau.reisewelle.dto.TripDetailsDTO;
import de.fhzwickau.reisewelle.dao.TripDao;

import java.sql.SQLException;
import java.util.UUID;

public class DetailTripController {

    @FXML private Label driverLabel;
    @FXML private Label busLabel;
    @FXML private ListView<String> stopsList;

    private final TripDao tripDao = new TripDao();

    public DetailTripController() throws SQLException {
    }

    public void loadTripDetails(UUID tripId) throws IllegalArgumentException, SQLException {
        TripDetailsDTO trip = tripDao.getTripDetails(tripId);
        if (trip == null) {
            driverLabel.setText("Keine Detail Informationen");
            throw new IllegalArgumentException("Trip kann nicht geöffnet werden");
        }

        driverLabel.setText(trip.getDriverName());
        busLabel.setText(trip.getBusNumber());
        stopsList.getItems().setAll(trip.getStops());
    }
}
