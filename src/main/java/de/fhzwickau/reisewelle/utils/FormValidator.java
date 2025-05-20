package de.fhzwickau.reisewelle.utils;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class FormValidator {

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
}

