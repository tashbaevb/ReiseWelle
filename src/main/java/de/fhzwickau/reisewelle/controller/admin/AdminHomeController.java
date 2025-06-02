package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.config.AccessManager;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import de.fhzwickau.reisewelle.utils.PermissionsUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class AdminHomeController {

    @FXML
    private StackPane contentPane;

    @FXML
    private void showUsers() {
        loadContent("user/admin-user.fxml", PermissionsUtil.KUNDEN_SEHEN);
    }

    @FXML
    private void showEmployee() {
        loadContent("employee/admin-employee.fxml", PermissionsUtil.MITARBEITER_VERWALTEN);
    }

    @FXML
    private void showBuses() {
        loadContent("bus/admin-bus.fxml", PermissionsUtil.BUSSE_VERWALTEN);
    }

    @FXML
    private void showDrivers() {
        loadContent("driver/admin-driver.fxml", PermissionsUtil.FARHER_VERWALTEN);
    }

    @FXML
    private void showTrips() {
        loadContent("trip/admin-trip.fxml", PermissionsUtil.REISEN_VERWALTEN);
    }

    @FXML
    private void showRoles() {
        loadContent("user_role/user-role.fxml", PermissionsUtil.ROLLEN_VERWALTEN);
    }

    @FXML
    private void showPermissions() {
        loadContent("permission/permission.fxml", PermissionsUtil.BERECHTIGUNGEN_VERWALTEN);
    }

    @FXML
    private void showRolePermissions() {
        loadContent("role-permission/user-role-permission.fxml", PermissionsUtil.ROLLE_BERECHTIGUNGEN_VERWALTEN);
    }

    @FXML
    private void showCountries() {
        loadContent("country/admin-country.fxml", PermissionsUtil.LAENDER_VERWALTEN);
    }

    @FXML
    public void showCities() {
        loadContent("city/admin-city.fxml", PermissionsUtil.STAEDTE_VERWALTEN);
    }

    @FXML
    public void showStatus() {
        loadContent("status/admin-status.fxml", PermissionsUtil.STAND_VERWALTEN);
    }

    @FXML
    public void showTripStatus() {
        loadContent("trip_status/admin-trip-status.fxml", PermissionsUtil.REISEN_STAND_VERWALTEN);
    }

    private void loadContent(String fxmlFile, String requiredPermission) {
//        if (!AccessManager.hasPermission(requiredPermission)) {
//            AlertUtil.showError("Zugriff verweigert", "Sie haben keine Berechtigung, dieses Fenster zu öffnen.");
//            return;
//        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/fhzwickau/reisewelle/admin/" + fxmlFile));
            Pane newContent = loader.load();
            contentPane.getChildren().setAll(newContent);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
