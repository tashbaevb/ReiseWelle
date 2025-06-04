package de.fhzwickau.reisewelle.controller;

import de.fhzwickau.reisewelle.config.AccessManager;
import de.fhzwickau.reisewelle.dao.UserDao;
import de.fhzwickau.reisewelle.dao.UserRoleDao;
import de.fhzwickau.reisewelle.dao.UserRolePermissionDao;
import de.fhzwickau.reisewelle.model.User;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import de.fhzwickau.reisewelle.utils.FormValidator;
import de.fhzwickau.reisewelle.utils.PasswordHasher;
import de.fhzwickau.reisewelle.utils.Session;
import de.fhzwickau.reisewelle.utils.WindowUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import java.time.LocalDateTime;
import java.util.Set;

public class RegistrationController {

    @FXML
    private TextField emailIdField;

    @FXML
    private PasswordField passwordField;

    private final UserDao userDao = new UserDao();
    private final UserRoleDao userRoleDao = new UserRoleDao();
    private final UserRolePermissionDao userRolePermissionDao = new UserRolePermissionDao();

    @FXML
    public void register(ActionEvent event) {
        Window owner = emailIdField.getScene().getWindow();

        if (FormValidator.hasEmptyFields(emailIdField, passwordField)) {
            AlertUtil.showError("Formularfehler", "Bitte füllen Sie alle Felder aus.");
            return;
        }

        try {
            String email = emailIdField.getText().trim();
            String password = passwordField.getText().trim();

            PasswordHasher.HashResult hashResult = PasswordHasher.hashPassword(password);

            User user = new User(
                    email,
                    hashResult.hashBase64(),
                    hashResult.saltBase64(),
                    userRoleDao.findByName("USER"),
                    LocalDateTime.now()
            );

            userDao.save(user);

            Session.getInstance().setCurrentUser(user);

            Set<String> permissions = userRolePermissionDao.findPermissionNamesByUserId(user.getId());
            AccessManager.setPermissions(permissions);

            AlertUtil.showAlert(
                    javafx.scene.control.Alert.AlertType.CONFIRMATION,
                    "Erfolg",
                    null,
                    "Willkommen " + email,
                    owner
            );

            WindowUtil.openWindow("/de/fhzwickau/reisewelle/user/user-main.fxml", "Benutzerbereich", event);
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Fehler", "Registrierung fehlgeschlagen: " + e.getMessage());
        }
    }

    @FXML
    public void openLoginWindow(javafx.scene.input.MouseEvent event) {
        WindowUtil.openWindow("/de/fhzwickau/reisewelle/login-page.fxml", "Login", event);
    }
}
