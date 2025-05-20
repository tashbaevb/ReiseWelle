package de.fhzwickau.reisewelle.controller.admin;

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
        loadContent("admin-user.fxml");
    }

    @FXML
    private void showEmployee() {
        loadContent("admin-employee.fxml");
    }

    @FXML
    private void showBuses() {
        loadContent("admin-bus.fxml");
    }

    @FXML
    private void showDrivers() {
        loadContent("admin-drivers.fxml");
    }

    @FXML
    private void showTrips() {
        loadContent("admin-trip.fxml");
    }

    private void loadContent(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/fhzwickau/reisewelle/admin/" + fxmlFile));
            Pane newContent = loader.load();
            contentPane.getChildren().setAll(newContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
