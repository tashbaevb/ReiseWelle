package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.model.Bus;
import de.fhzwickau.reisewelle.model.Status;
import de.fhzwickau.reisewelle.dao.BusDao;
import de.fhzwickau.reisewelle.dao.StatusDao;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class AddEditBusController {

    @FXML private TextField busNumberField;
    @FXML private TextField totalSeatsField;
    @FXML private ComboBox<Status> statusComboBox;

    private Bus bus;
    private final BusDao busDao = new BusDao();
    private final StatusDao statusDao = new StatusDao();

    public void setBus(Bus bus) {
        this.bus = bus;
        if (bus != null) {
            busNumberField.setText(bus.getBusNumber());
            totalSeatsField.setText(String.valueOf(bus.getTotalSeats()));
            statusComboBox.setValue(bus.getStatus());
        }
    }

    @FXML
    private void initialize() throws SQLException {
        statusComboBox.getItems().addAll(statusDao.findAll());
        System.out.println("Loaded statuses: " + statusComboBox.getItems().size());
    }

    @FXML
    private void save() throws SQLException {
        System.out.println("Save button clicked");
        if (bus == null) {
            try {
                String busNumber = busNumberField.getText();
                int totalSeats = Integer.parseInt(totalSeatsField.getText().trim());
                Status status = statusComboBox.getValue();
                if (busNumber == null || busNumber.trim().isEmpty() || status == null) {
                    System.out.println("Validation failed: Bus number or status is empty");
                    return;
                }
                bus = new Bus(busNumber, totalSeats, status);
                System.out.println("Creating new bus: number=" + busNumber + ", seats=" + totalSeats + ", status=" + status.getName());
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid total seats value: " + totalSeatsField.getText());
                return;
            }
        } else {
            bus.setBusNumber(busNumberField.getText());
            try {
                bus.setTotalSeats(Integer.parseInt(totalSeatsField.getText().trim()));
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid total seats value: " + totalSeatsField.getText());
                return;
            }
            bus.setStatus(statusComboBox.getValue());
            System.out.println("Updating bus: number=" + bus.getBusNumber() + ", seats=" + bus.getTotalSeats() + ", status=" + bus.getStatus().getName());
        }
        busDao.save(bus);
        System.out.println("Save completed, closing window");
        close();
    }

    @FXML
    private void cancel() {
        System.out.println("Cancel button clicked");
        close();
    }

    private void close() {
        Stage stage = (Stage) busNumberField.getScene().getWindow();
        stage.close();
    }
}