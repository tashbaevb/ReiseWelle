package de.fhzwickau.reisewelle.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import de.fhzwickau.reisewelle.model.TripDetailsDTO;
import de.fhzwickau.reisewelle.repository.TripRepository;

import java.util.UUID;

public class DetailTripController {

    @FXML private Label driverLabel;
    @FXML private Label busLabel;
    @FXML private Label availableSeatsLabel;
    @FXML private ListView<String> stopsList;

    private final TripRepository tripRepository = new TripRepository();

    public void loadTripDetails(UUID tripId) {
        TripDetailsDTO trip = tripRepository.getTripDetails(tripId);
        if (trip != null) {
            driverLabel.setText(trip.getDriverName());
            busLabel.setText(trip.getBusNumber());
            availableSeatsLabel.setText("Свободно мест: " + trip.getAvailableSeats());
            stopsList.getItems().setAll(trip.getStops());
        } else {
            driverLabel.setText("Поездка не найдена");
        }
    }
}
