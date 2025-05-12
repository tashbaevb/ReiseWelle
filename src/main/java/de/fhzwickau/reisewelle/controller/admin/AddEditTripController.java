package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.model.Bus;
import de.fhzwickau.reisewelle.model.Driver;
import de.fhzwickau.reisewelle.model.Trip;
import de.fhzwickau.reisewelle.model.TripStatus;
import de.fhzwickau.reisewelle.repository.BusRepository;
import de.fhzwickau.reisewelle.repository.DriverRepository;
import de.fhzwickau.reisewelle.repository.TripAdminRepository;
import de.fhzwickau.reisewelle.repository.TripStatusRepository;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

public class AddEditTripController {

    @FXML private ComboBox<Bus> busComboBox;
    @FXML private ComboBox<Driver> driverComboBox;
    @FXML private DatePicker departureDatePicker;
    @FXML private ComboBox<TripStatus> statusComboBox;

    private Trip trip;
    private TripAdminRepository tripRepository = new TripAdminRepository();
    private BusRepository busRepository = new BusRepository();
    private DriverRepository driverRepository = new DriverRepository();
    private TripStatusRepository tripStatusRepository = new TripStatusRepository();

    public void setTrip(Trip trip) {
        this.trip = trip;
        if (trip != null) {
            busComboBox.setValue(trip.getBus());
            driverComboBox.setValue(trip.getDriver());
            departureDatePicker.setValue(trip.getDepartureDate());
            statusComboBox.setValue(trip.getStatus());
        }
    }

    @FXML
    private void initialize() {
        busComboBox.getItems().addAll(busRepository.findAll());
        driverComboBox.getItems().addAll(driverRepository.findAll());
        statusComboBox.getItems().addAll(tripStatusRepository.findAll());
    }

    @FXML
    private void save() {
        if (trip == null) {
            trip = new Trip(
                    busComboBox.getValue(),
                    driverComboBox.getValue(),
                    departureDatePicker.getValue(),
                    statusComboBox.getValue()
            );
        } else {
            trip.setBus(busComboBox.getValue());
            trip.setDriver(driverComboBox.getValue());
            trip.setDepartureDate(departureDatePicker.getValue());
            trip.setStatus(statusComboBox.getValue());
        }
        tripRepository.save(trip);
        close();
    }

    @FXML
    private void cancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) busComboBox.getScene().getWindow();
        stage.close();
    }
}