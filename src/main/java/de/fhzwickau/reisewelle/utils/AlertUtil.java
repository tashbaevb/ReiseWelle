package de.fhzwickau.reisewelle.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

import java.util.Optional;
import java.util.function.Consumer;

public class AlertUtil {

    public static void showError(String header, String content) {
        showAlert(Alert.AlertType.ERROR, null, header, content, null);
    }

    public static void showInfo(String header, String content) {
        showAlert(Alert.AlertType.INFORMATION, null, header, content, null);
    }

    public static void showError(String title, String header, String content, Window owner) {
        showAlert(Alert.AlertType.ERROR, title, header, content, owner);
    }

    public static void showAlert(Alert.AlertType alertType, String title, String header, String content, Window owner) {
        Alert alert = new Alert(alertType);
        if (title != null) alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        if (owner != null) alert.initOwner(owner);
        alert.showAndWait();
    }

    public static boolean showConfirmation(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(header);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static void showConfirmation(String message, Consumer<Boolean> callback) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(message);
        alert.showAndWait().ifPresent(response -> {
            callback.accept(response == ButtonType.OK);
        });
    }
}
