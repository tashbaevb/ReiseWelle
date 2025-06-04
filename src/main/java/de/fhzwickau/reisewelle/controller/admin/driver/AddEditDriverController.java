package de.fhzwickau.reisewelle.controller.admin.driver;

import de.fhzwickau.reisewelle.controller.admin.BaseAddEditController;
import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.model.Driver;
import de.fhzwickau.reisewelle.model.Status;
import de.fhzwickau.reisewelle.dao.DriverDao;
import de.fhzwickau.reisewelle.dao.StatusDao;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import de.fhzwickau.reisewelle.utils.ComboBoxUtils;
import de.fhzwickau.reisewelle.utils.FormValidator;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class AddEditDriverController extends BaseAddEditController<Driver> {

    @FXML
    private TextField firstNameField, lastNameField, licenseNumberField;
    @FXML
    private ComboBox<Status> statusComboBox;

    private final BaseDao<Driver> driverDao = new DriverDao();
    private final BaseDao<Status> statusDao = new StatusDao();

    @FXML
    private void initialize() {
        ComboBoxUtils.populate(statusComboBox, statusDao);
    }

    @Override
    protected void saveEntity() throws SQLException {
        if (FormValidator.areFieldsEmpty(firstNameField, lastNameField, licenseNumberField) || FormValidator.isComboBoxEmpty(statusComboBox)) {
            AlertUtil.showError("Fehler", "Die Validierung ist fehlgeschlagen. Bitte füllen Sie alle Felder aus.");
            return;
        }

        if (entity == null) {
            entity = new Driver(firstNameField.getText(), lastNameField.getText(), licenseNumberField.getText(), statusComboBox.getValue());
        } else {
            entity.setFirstName(firstNameField.getText());
            entity.setLastName(lastNameField.getText());
            entity.setLicenseNumber(licenseNumberField.getText());
            entity.setStatus(statusComboBox.getValue());
        }

        try {
            driverDao.save(entity);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    @Override
    protected Node getAnyControl() {
        return firstNameField;
    }

    public void setDriver(Driver driver) {
        this.entity = driver;
        if (driver != null) {
            firstNameField.setText(driver.getFirstName());
            lastNameField.setText(driver.getLastName());
            licenseNumberField.setText(driver.getLicenseNumber());
            statusComboBox.setValue(driver.getStatus());
        }
    }
}