package de.fhzwickau.reisewelle.controller;

import de.fhzwickau.reisewelle.config.AccessManager;
import de.fhzwickau.reisewelle.dao.UserRolePermissionDao;
import de.fhzwickau.reisewelle.model.Authenticatable;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import de.fhzwickau.reisewelle.utils.FormValidator;
import de.fhzwickau.reisewelle.utils.PasswordHasher;
import de.fhzwickau.reisewelle.utils.Session;
import de.fhzwickau.reisewelle.utils.WindowUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Set;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private final AuthenticationService authService = new AuthenticationService();
    private final UserRolePermissionDao userRolePermissionBaseDao = new UserRolePermissionDao();

    public void login(ActionEvent event) {
        if (FormValidator.hasEmptyFields(emailField, passwordField)) {
            AlertUtil.showError("Login Fehler", "Bitte geben Sie E-Mail und Passwort ein.");
            return;
        }

        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        try {
            Authenticatable user = authService.findByEmail(email);
            if (user == null) {
                AlertUtil.showError("Login fehlgeschlagen", "E-Mail wurde nicht gefunden.");
                return;
            }

            boolean passwordMatch = PasswordHasher.verifyPassword(password, user.getPassword(), user.getSalt());

            if (!passwordMatch) {
                AlertUtil.showError("Login fehlgeschlagen", "Falsches Passwort.");
                return;
            }

            Session.getInstance().setCurrentUser(user);
            String role = user.getUserRole().toString().toUpperCase();
            System.out.println(role);

            Set<String> permissions = userRolePermissionBaseDao.findPermissionNamesByUserId(user.getId());
            AccessManager.setPermissions(permissions);

            switch (role) {
                case "ADMIN":
                    WindowUtil.openWindow("/de/fhzwickau/reisewelle/admin/admin-home-page.fxml", "Admin Dashboard", event);
                    break;
                case "EMPLOYEE":
                    WindowUtil.openWindow("/de/fhzwickau/reisewelle/admin/admin-home-page.fxml", "Mitarbeiter Dashboard", event);
                    break;
                case "USER":
                    WindowUtil.openWindow("/de/fhzwickau/reisewelle/user/user-profile-page.fxml", "Benutzerbereich", event);
                    break;
                default:
                    AlertUtil.showError("Unbekannte Rolle", "Unbekannter Benutzerrolle: " + role);
            }

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Fehler", "Ein Fehler ist aufgetreten: " + e.getMessage());
        }
    }

    @FXML
    private void openRegisterWindow(javafx.scene.input.MouseEvent event) {
        WindowUtil.openWindow("/de/fhzwickau/reisewelle/registration-page.fxml", "Registrierung", event);
    }
}
