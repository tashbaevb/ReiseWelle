package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.model.Bus;
import de.fhzwickau.reisewelle.model.Status;
import de.fhzwickau.reisewelle.repository.BusRepository;
import de.fhzwickau.reisewelle.repository.StatusRepository;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddEditBusController {

    @FXML private TextField busNumberField;
    @FXML private TextField totalSeatsField;
    @FXML private ComboBox<Status> statusComboBox;

    private Bus bus;
    private BusRepository busRepository = new BusRepository();
    private StatusRepository statusRepository = new StatusRepository();

    public void setBus(Bus bus) {
        this.bus = bus;
        if (bus != null) {
            busNumberField.setText(bus.getBusNumber());
            totalSeatsField.setText(String.valueOf(bus.getTotalSeats()));
            statusComboBox.setValue(bus.getStatus());
        }
    }

    @FXML
    private void initialize() {
        statusComboBox.getItems().addAll(statusRepository.findAll());
        System.out.println("Loaded statuses: " + statusComboBox.getItems().size());
    }

    @FXML
    private void save() {
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
        busRepository.save(bus);
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