package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.model.Driver;
import de.fhzwickau.reisewelle.model.Status;
import de.fhzwickau.reisewelle.dao.DriverDao;
import de.fhzwickau.reisewelle.dao.StatusDao;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class AddEditDriverController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField licenseNumberField;
    @FXML private ComboBox<Status> statusComboBox;

    private Driver driver;
    private final DriverDao driverDao = new DriverDao();
    private final StatusDao statusDao = new StatusDao();

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
    private void initialize() throws SQLException {
        statusComboBox.getItems().addAll(statusDao.findAll());
    }

    @FXML
    private void save() throws SQLException {
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
        driverDao.save(driver);
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