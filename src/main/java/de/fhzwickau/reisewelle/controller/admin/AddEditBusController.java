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

public class AddEditBusController extends BaseAddEditController<Bus> {

    @FXML private TextField busNumberField;
    @FXML private TextField totalSeatsField;
    @FXML private ComboBox<Status> statusComboBox;

    private final BaseDao<Bus> busDao = new BusDao();
    private final BaseDao<Status> statusDao = new StatusDao();

    @FXML
    private void initialize() throws SQLException {
        statusComboBox.getItems().setAll(statusDao.findAll());
    }

    @Override
    protected void saveEntity() throws SQLException {
        String busNumber = busNumberField.getText();
        Integer totalSeats = FormValidator.parseInteger(totalSeatsField);
        Status status = statusComboBox.getValue();

        if (FormValidator.isFieldEmpty(busNumberField) || totalSeats == null || status == null) {
            System.out.println("Validation failed");
            return;
        }

        if (entity == null) {
            entity = new Bus(busNumber, totalSeats, status);
        } else {
            entity.setBusNumber(busNumber);
            entity.setTotalSeats(totalSeats);
            entity.setStatus(status);
        }

        busDao.save(entity);
    }

    @Override
    protected Node getAnyControl() {
        return busNumberField;
    }

    public void setBus(Bus bus) {
        this.entity = bus;
        if (bus != null) {
            busNumberField.setText(bus.getBusNumber());
            totalSeatsField.setText(String.valueOf(bus.getTotalSeats()));
            statusComboBox.setValue(bus.getStatus());
        }
    }
}

