package de.fhzwickau.reisewelle.utils;

import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class CustomDateTimePicker extends HBox {
    private final DatePicker datePicker = new DatePicker();
    private final Spinner<Integer> hourSpinner = new Spinner<>(0, 23, 12);
    private final Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0);

    public CustomDateTimePicker() {
        setSpacing(5);
        hourSpinner.setPrefWidth(60);
        minuteSpinner.setPrefWidth(60);
        getChildren().addAll(datePicker, new Label("H:"), hourSpinner, new Label("M:"), minuteSpinner);
    }

    public LocalDateTime getDateTimeValue() {
        if (datePicker.getValue() == null) return null;
        return LocalDateTime.of(datePicker.getValue(), LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue()));
    }

    public void setDateTimeValue(LocalDateTime value) {
        if (value != null) {
            datePicker.setValue(value.toLocalDate());
            hourSpinner.getValueFactory().setValue(value.getHour());
            minuteSpinner.getValueFactory().setValue(value.getMinute());
        }
    }
}
