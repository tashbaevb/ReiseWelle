package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.model.Bus;
import de.fhzwickau.reisewelle.model.Driver;
import de.fhzwickau.reisewelle.model.Trip;
import de.fhzwickau.reisewelle.model.TripStatus;
import de.fhzwickau.reisewelle.dao.BusDao;
import de.fhzwickau.reisewelle.dao.DriverDao;
import de.fhzwickau.reisewelle.dao.TripAdminDao;
import de.fhzwickau.reisewelle.dao.TripStatusDao;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.sql.SQLException;

public class AddEditTripController {

    @FXML private ComboBox<Bus> busComboBox;
    @FXML private ComboBox<Driver> driverComboBox;
    @FXML private DatePicker departureDatePicker;
    @FXML private ComboBox<TripStatus> statusComboBox;

    private Trip trip;
    private final TripAdminDao tripRepository = new TripAdminDao();
    private final BusDao busDao = new BusDao();
    private final DriverDao driverDao = new DriverDao();
    private final TripStatusDao tripStatusDao = new TripStatusDao();

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
    private void initialize() throws SQLException {
        busComboBox.getItems().addAll(busDao.findAll());
        driverComboBox.getItems().addAll(driverDao.findAll());
        statusComboBox.getItems().addAll(tripStatusDao.findAll());
    }

    @FXML
    private void save() throws SQLException {
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