package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.utils.WindowUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class AdminNavbarController {

//    @FXML
//    private void goMain(ActionEvent e) {
//        // Можно реализовать возврат к главной странице, если нужно
//        // Например, обновить центральную панель/StackPane
//    }

    @FXML
    private void logout(ActionEvent event) {
        // Закрываем текущее окно если есть event
        if (event != null && event.getSource() instanceof javafx.scene.Node node) {
            javafx.stage.Stage stage = (javafx.stage.Stage) node.getScene().getWindow();
            stage.close();
        }
        // Открываем логин-страницу
        de.fhzwickau.reisewelle.utils.WindowUtil.openWindow(
                "/de/fhzwickau/reisewelle/login-page.fxml", "Login", null);
    }
}
