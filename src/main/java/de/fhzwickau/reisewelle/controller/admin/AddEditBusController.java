package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.model.Bus;
import de.fhzwickau.reisewelle.model.Status;
import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.BusDao;
import de.fhzwickau.reisewelle.dao.StatusDao;
import de.fhzwickau.reisewelle.utils.FormValidator;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.sql.SQLException;

/**
 * Controller for Add/Edit Bus dialog.
 */
public class AddEditBusController extends BaseAddEditController<Bus> {

    @FXML private TextField busNumberField;
    @FXML private TextField totalSeatsField;
    @FXML private TextField bikeSpacesField;    // new field for bike spaces
    @FXML private ComboBox<Status> statusComboBox;

    private final BaseDao<Bus>    busDao    = new BusDao();
    private final BaseDao<Status> statusDao = new StatusDao();

    @FXML
    private void initialize() throws SQLException {
        // Load statuses into combo box
        statusComboBox.getItems().setAll(statusDao.findAll());
    }

    @Override
    protected void saveEntity() throws SQLException {
        // collect and validate input
        String busNumber   = busNumberField.getText();
        Integer totalSeats = FormValidator.parseInteger(totalSeatsField);
        Integer bikeSpaces = FormValidator.parseInteger(bikeSpacesField);
        Status  status     = statusComboBox.getValue();

        if (FormValidator.isFieldEmpty(busNumberField)
                || totalSeats == null
                || bikeSpaces == null
                || status == null) {
            // you can show an error dialog here if needed
            return;
        }

        // create new or update existing
        if (entity == null) {
            entity = new Bus(busNumber, totalSeats, status, bikeSpaces);
        } else {
            entity.setBusNumber(busNumber);
            entity.setTotalSeats(totalSeats);
            entity.setBikeSpaces(bikeSpaces);
            entity.setStatus(status);
        }

        // save to database
        busDao.save(entity);
    }

    @Override
    protected Node getAnyControl() {
        return busNumberField;
    }

    /**
     * Populate form fields when editing an existing Bus.
     */
    public void setBus(Bus bus) {
        this.entity = bus;
        if (bus != null) {
            busNumberField.setText(bus.getBusNumber());
            totalSeatsField.setText(String.valueOf(bus.getTotalSeats()));
            bikeSpacesField.setText(String.valueOf(bus.getBikeSpaces()));
            statusComboBox.setValue(bus.getStatus());
        }
    }
}
