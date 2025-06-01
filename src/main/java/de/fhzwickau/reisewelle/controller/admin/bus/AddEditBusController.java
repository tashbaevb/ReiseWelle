package de.fhzwickau.reisewelle.controller.admin.bus;

import de.fhzwickau.reisewelle.controller.admin.BaseAddEditController;
import de.fhzwickau.reisewelle.model.Bus;
import de.fhzwickau.reisewelle.model.Status;
import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.BusDao;
import de.fhzwickau.reisewelle.dao.StatusDao;
import de.fhzwickau.reisewelle.utils.ComboBoxUtils;
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

    @FXML
    private TextField busNumberField, totalSeatsField, bikeSpacesField;
    @FXML
    private ComboBox<Status> statusComboBox;

    private final BaseDao<Bus> busDao = new BusDao();
    private final BaseDao<Status> statusDao = new StatusDao();

    @FXML
    private void initialize() {
        ComboBoxUtils.populate(statusComboBox, statusDao);
    }

    @Override
    protected void saveEntity() throws SQLException {
        if (FormValidator.areFieldsEmpty(busNumberField, totalSeatsField, bikeSpacesField) || FormValidator.isComboBoxEmpty(statusComboBox)) {
            throw new IllegalArgumentException("Die Validierung ist fehlgeschlagen. Bitte füllen Sie alle Felder aus.");
        }

        String busNumber = busNumberField.getText();
        Integer totalSeats = FormValidator.parseInteger(totalSeatsField);
        Integer bikeSpaces = FormValidator.parseInteger(bikeSpacesField);
        Status status = statusComboBox.getValue();

        if (entity == null) {
            entity = new Bus(busNumber, totalSeats, status, bikeSpaces);
        } else {
            entity.setBusNumber(busNumber);
            entity.setTotalSeats(totalSeats);
            entity.setBicycleSpaces(bikeSpaces);
            entity.setStatus(status);
        }

        try {
            busDao.save(entity);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
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
            bikeSpacesField.setText(String.valueOf(bus.getBicycleSpaces()));
            statusComboBox.setValue(bus.getStatus());
        }
    }
}
