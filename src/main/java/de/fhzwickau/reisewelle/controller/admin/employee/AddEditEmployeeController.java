package de.fhzwickau.reisewelle.controller.admin.employee;

import de.fhzwickau.reisewelle.controller.admin.BaseAddEditController;
import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.EmployeeDao;
import de.fhzwickau.reisewelle.model.Employee;
import de.fhzwickau.reisewelle.model.User;
import de.fhzwickau.reisewelle.model.UserRole;
import de.fhzwickau.reisewelle.dao.UserRoleDao;
import de.fhzwickau.reisewelle.utils.FormValidator;
import de.fhzwickau.reisewelle.utils.PasswordHasher;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class AddEditEmployeeController extends BaseAddEditController<Employee> {

    @FXML
    private TextField emailField, passwordField, vornameField, nachnameField;
    @FXML
    private Label passwordLabel;

    private static final String EMPLOYEE_ROLE_NAME = "Mitarbeiter";
    private static final Logger logger = LoggerFactory.getLogger(User.class);
    private final BaseDao<Employee> employeeDao = new EmployeeDao();
    private final UserRoleDao userRoleDao = new UserRoleDao();
    private UserRole employeeRole;

    @FXML
    private void initialize() throws SQLException {
        employeeRole = userRoleDao.findByName(EMPLOYEE_ROLE_NAME);
    }

    @Override
    protected void saveEntity() {
        if (validateInput()) {
            String vorname = vornameField.getText();
            String nachname = nachnameField.getText();
            String email = emailField.getText();

            try {
                if (entity == null) {
                    String rawPassword = passwordField.getText();
                    PasswordHasher.HashResult hashResult = PasswordHasher.hashPassword(rawPassword);

                    entity = new Employee(vorname, nachname, email, hashResult.hashBase64(), hashResult.saltBase64(), employeeRole);
                    logger.info("Neuen Mitarbeiter speichern: email=" + entity.getEmail());
                } else {
                    entity.setVorname(vorname);
                    entity.setNachname(nachname);
                    entity.setEmail(email);
                    entity.setUserRole(employeeRole);
                    logger.info("Mitarbeiter aktualisieren: id=" + entity.getId());
                }
                employeeDao.save(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected Node getAnyControl() {
        return emailField;
    }

    public void setEmployee(Employee employee) {
        this.entity = employee;

        if (employee != null) {
            vornameField.setText(employee.getVorname());
            nachnameField.setText(employee.getNachname());
            emailField.setText(employee.getEmail());

            passwordField.setVisible(false);
            passwordField.setManaged(false);
            passwordLabel.setVisible(false);
            passwordLabel.setManaged(false);
        }
    }

    private boolean validateInput() {
        boolean missing = FormValidator.areFieldsEmpty(vornameField, nachnameField, emailField);
        if (entity == null && FormValidator.areFieldsEmpty(passwordField)) {
            missing = true;
        }
        if (missing) {
            logger.warn("Validierung fehlgeschlagen: Felder leer");
        }
        return !missing;
    }
}
