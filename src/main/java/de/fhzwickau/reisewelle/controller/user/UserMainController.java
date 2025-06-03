package de.fhzwickau.reisewelle.controller.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane; // поменяли тут
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class UserMainController {

    @FXML
    private StackPane contentContainer; // поменяли тут

    @FXML
    private UserNavbarController navbarController;

    @FXML
    public void initialize() {
        // Связываем navbar с этим контроллером
        if (navbarController != null)
            navbarController.setMainController(this);
        // Загружаем страницу trips по умолчанию
        loadContent("/de/fhzwickau/reisewelle/user/trips_page.fxml");
    }

    public void loadContent(String fxmlPath) {
        try {
            Parent page = FXMLLoader.load(getClass().getResource(fxmlPath)); // теперь Parent, можно AnchorPane/StackPane
            contentContainer.getChildren().setAll(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logoutToLogin() {
        // Открыть login window и закрыть текущее окно пользователя
        try {
            Parent loginPage = FXMLLoader.load(getClass().getResource("/de/fhzwickau/reisewelle/login-page.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loginPage));
            stage.setTitle("Login");
            stage.show();
            // Закрываем старое окно
            Stage oldStage = (Stage) contentContainer.getScene().getWindow();
            oldStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
