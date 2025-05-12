package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.model.Driver;
import de.fhzwickau.reisewelle.model.Status;
import de.fhzwickau.reisewelle.repository.DriverRepository;
import de.fhzwickau.reisewelle.repository.StatusRepository;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddEditDriverController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField licenseNumberField;
    @FXML private ComboBox<Status> statusComboBox;

    private Driver driver;
    private DriverRepository driverRepository = new DriverRepository();
    private StatusRepository statusRepository = new StatusRepository();

    public void setDriver(Driver driver) {
        this.driver = driver;
        if (driver != null) {
            firstNameField.setText(driver.getFirstName());
            lastNameField.setText(driver.getLastName());
            licenseNumberField.setText(driver.getLicenseNumber());
            statusComboBox.setValue(driver.getStatus());
        }
    }

    @FXML
    private void initialize() {
        statusComboBox.getItems().addAll(statusRepository.findAll());
    }

    @FXML
    private void save() {
        if (driver == null) {
            driver = new Driver(
                    firstNameField.getText(),
                    lastNameField.getText(),
                    licenseNumberField.getText(),
                    statusComboBox.getValue()
            );
        } else {
            driver.setFirstName(firstNameField.getText());
            driver.setLastName(lastNameField.getText());
            driver.setLicenseNumber(licenseNumberField.getText());
            driver.setStatus(statusComboBox.getValue());
        }
        driverRepository.save(driver);
        close();
    }

    @FXML
    private void cancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) firstNameField.getScene().getWindow();
        stage.close();
    }
}