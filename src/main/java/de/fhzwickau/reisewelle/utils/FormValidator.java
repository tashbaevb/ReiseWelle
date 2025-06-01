package de.fhzwickau.reisewelle.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class FormValidator {

    public static boolean areFieldsEmpty(TextField... fields) {
        for (TextField field : fields) {
            if (field.getText() == null || field.getText().trim().isEmpty()) return true;
        }
        return false;
    }

    public static boolean isFieldEmpty(TextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    public static boolean isComboBoxEmpty(ComboBox<?> comboBox) {
        return comboBox.getValue() == null;
    }

    public static Integer parseInteger(TextField field) {
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static <T> boolean validateInput(TableView<T> tableView, ComboBox<?>... comboBoxes) {
        for (ComboBox<?> comboBox : comboBoxes) {
            if (comboBox.getValue() == null) {
                showError("Validierung fehlgeschlagen", "Bitte füllen Sie alle Pflichtfelder aus.");
                return false;
            }
        }

        if (tableView.getItems().size() < 2) {
            showError("Zu wenige Haltestellen", "Eine Fahrt muss mindestens zwei Haltestellen haben.");
            return false;
        }
        return true;
    }

    public static void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
