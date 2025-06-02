package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.config.DatabaseXMLExport;
import de.fhzwickau.reisewelle.utils.WindowUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class AdminNavbarController {

    @FXML
    private void exportXml(ActionEvent event) {
        try {
            // Выбор места для сохранения
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exportiere Datenbank als XML");
            fileChooser.setInitialFileName("DB_EXPORT.xml");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"));

            Stage stage = null;
            if (event.getSource() instanceof javafx.scene.Node node) {
                stage = (Stage) node.getScene().getWindow();
            }
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                boolean success = DatabaseXMLExport.exportToXml(file.getAbsolutePath());
                Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                alert.setTitle(success ? "Erfolg" : "Fehler");
                alert.setHeaderText(null);
                alert.setContentText(success ? "Export erfolgreich:\n" + file.getAbsolutePath() : "Export fehlgeschlagen!");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Fehler beim Export:\n" + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        // Закрываем текущее окно если есть event
        if (event != null && event.getSource() instanceof javafx.scene.Node node) {
            javafx.stage.Stage stage = (javafx.stage.Stage) node.getScene().getWindow();
            stage.close();
        }
        // Открываем логин-страницу
        WindowUtil.openWindow(
                "/de/fhzwickau/reisewelle/login-page.fxml", "Login", null);
    }
}
