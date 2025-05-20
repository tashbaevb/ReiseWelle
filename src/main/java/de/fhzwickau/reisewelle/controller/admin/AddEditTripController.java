package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.model.Bus;
import de.fhzwickau.reisewelle.model.Driver;
import de.fhzwickau.reisewelle.model.Trip;
import de.fhzwickau.reisewelle.model.TripStatus;
import de.fhzwickau.reisewelle.dao.BusDao;
import de.fhzwickau.reisewelle.dao.DriverDao;
import de.fhzwickau.reisewelle.dao.TripAdminDao;
import de.fhzwickau.reisewelle.dao.TripStatusDao;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import java.sql.SQLException;

public class AddEditTripController extends BaseAddEditController<Trip>{

    @FXML private ComboBox<Bus> busComboBox;
    @FXML private ComboBox<Driver> driverComboBox;
    @FXML private DatePicker departureDatePicker;
    @FXML private ComboBox<TripStatus> statusComboBox;

    private final BaseDao<Trip> tripDao = new TripAdminDao();
    private final BaseDao<Bus> busDao = new BusDao();
    private final BaseDao<Driver> driverDao = new DriverDao();
    private final BaseDao<TripStatus> tripStatusDao = new TripStatusDao();

    @FXML
    private void initialize() throws SQLException {
        busComboBox.getItems().addAll(busDao.findAll());
        driverComboBox.getItems().addAll(driverDao.findAll());
        statusComboBox.getItems().addAll(tripStatusDao.findAll());
    }

    @Override
    protected void saveEntity() throws SQLException {
        if (entity == null) {
            entity = new Trip(
                    busComboBox.getValue(),
                    driverComboBox.getValue(),
                    departureDatePicker.getValue(),
                    statusComboBox.getValue()
            );
        } else {
            entity.setBus(busComboBox.getValue());
            entity.setDriver(driverComboBox.getValue());
            entity.setDepartureDate(departureDatePicker.getValue());
            entity.setStatus(statusComboBox.getValue());
        }
        tripDao.save(entity);
        close();
    }

    @Override
    protected Node getAnyControl() {
        return busComboBox;
    }

    public void setTrip(Trip trip) {
        this.entity = trip;
        if (trip != null) {
            busComboBox.setValue(trip.getBus());
            driverComboBox.setValue(trip.getDriver());
            departureDatePicker.setValue(trip.getDepartureDate());
            statusComboBox.setValue(trip.getStatus());
        }
    }
}