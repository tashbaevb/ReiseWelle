package de.fhzwickau.reisewelle.controller;

import de.fhzwickau.reisewelle.ReiseWelleApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private void onTripsButtonClick(ActionEvent event) {
        openNewWindow(event, "trips_page.fxml", "Trips Page");
    }

    @FXML
    protected void onAdminButtonClick(ActionEvent event) {
        openNewWindow(event, "/de/fhzwickau/reisewelle/admin/admin-home-page.fxml", "Admin Home Page");
    }

    private void openNewWindow(ActionEvent event, String fxmlFileName, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ReiseWelleApplication.class.getResource(fxmlFileName));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage homeStage = new Stage();
            homeStage.setTitle(title);
            homeStage.setScene(scene);
            homeStage.show();

            Stage loginStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}