package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.model.User;
import de.fhzwickau.reisewelle.model.UserRole;
import de.fhzwickau.reisewelle.dao.UserDao;
import de.fhzwickau.reisewelle.dao.UserRoleDao;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class AddEditUserController extends BaseAddEditController<User> {

    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private ComboBox<UserRole> roleComboBox;

    private static final Logger logger = LoggerFactory.getLogger(User.class);
    private final BaseDao<User> userDao = new UserDao();
    private final BaseDao<UserRole> userRoleDao = new UserRoleDao();
    private User user;
    private UserRole userRole;

    @FXML
    private void initialize() throws SQLException {
        List<UserRole> roles = userRoleDao.findAll();
        for (UserRole role : roles) {
            logger.info("Loaded role: id=" + role.getId() + ", name=" + role.getRoleName());
        }
        roleComboBox.getItems().setAll(roles);
    }

    @Override
    protected void saveEntity() throws SQLException {
        if (validateInput()) {
            if (entity == null) {
                String email = emailField.getText();
                String password = passwordField.getText();
                UserRole selectedRole = roleComboBox.getValue();
                if (selectedRole == null) {
                    logger.warn("Error: No role selected");
                    return;
                }
                entity = new User(email, password, selectedRole, LocalDateTime.now());
                logger.info("Saving new user: email=" + entity.getEmail() + ", role=" + entity.getUserRole().getRoleName());
            } else {
                entity.setEmail(emailField.getText());
                entity.setPassword(passwordField.getText());
                UserRole selectedRole = roleComboBox.getValue();
                if (selectedRole != null) {
                    entity.setUserRole(selectedRole);
                }
                logger.info("Updating user: email=" + entity.getEmail() + ", role=" + entity.getUserRole().getRoleName());
            }
            userDao.save(entity);
        }
    }

    @Override
    protected Node getAnyControl() {
        return emailField;
    }

    private boolean validateInput() {
        String email = emailField.getText();
        String password = passwordField.getText();
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty() || roleComboBox.getValue() == null) {
            logger.warn("Validation failed: Empty fields or no role selected");
            return false;
        }
        return true;
    }

    public void setUser(User user) {
        this.user = user;

        if (user != null) {
            emailField.setText(user.getEmail());
            passwordField.setText(user.getPassword());
            roleComboBox.setValue(user.getUserRole());
        }
    }

    public void setFixedUserRole(UserRole role) {
        this.userRole = role;
    }
}