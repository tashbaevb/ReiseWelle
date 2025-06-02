package de.fhzwickau.reisewelle.controller.admin.ticket;

import de.fhzwickau.reisewelle.dao.*;
import de.fhzwickau.reisewelle.model.*;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class AddEditTicketController {

    @FXML
    private ComboBox<User> userComboBox;
    @FXML
    private ComboBox<Trip> tripComboBox;
    @FXML
    private ComboBox<Stop> startStopComboBox;
    @FXML
    private ComboBox<Stop> endStopComboBox;
    @FXML
    private Spinner<Integer> adultsSpinner, childrenSpinner, bikesSpinner;
    @FXML
    private TextField priceField;
    @FXML
    private DatePicker purchaseDatePicker;

    private Ticket ticket;
    private Runnable onSaved;

    @FXML
    public void initialize() {
        // Заполняем комбобоксы
        try {
            List<User> users = new UserDao().findAll();
            userComboBox.setItems(FXCollections.observableList(users));
            List<Trip> trips = new TripAdminDao().findAll();
            tripComboBox.setItems(FXCollections.observableList(trips));

            // Обновлять список остановок при смене поездки
            tripComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateStops());

        } catch (Exception e) {
            AlertUtil.showError("Fehler", "Fehler beim Laden von Benutzern oder Fahrten: " + e.getMessage());
        }

        adultsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 30, 1));
        childrenSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 30, 0));
        bikesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0));
    }

    private void updateStops() {
        Trip selectedTrip = tripComboBox.getValue();
        if (selectedTrip != null) {
            try {
                List<Stop> stops = new StopDao().findByTripId(selectedTrip.getId());
                startStopComboBox.setItems(FXCollections.observableList(stops));
                endStopComboBox.setItems(FXCollections.observableList(stops));
            } catch (Exception e) {
                AlertUtil.showError("Fehler", "Fehler beim Laden von Stopps: " + e.getMessage());
            }
        } else {
            startStopComboBox.getItems().clear();
            endStopComboBox.getItems().clear();
        }
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
        if (ticket != null) {
            userComboBox.setValue(ticket.getUser());
            tripComboBox.setValue(ticket.getTrip());
            // После выбора trip обновить остановки и выставить их
            if (ticket.getTrip() != null) {
                updateStops();
                startStopComboBox.setValue(ticket.getStartStop());
                endStopComboBox.setValue(ticket.getEndStop());
            }
            adultsSpinner.getValueFactory().setValue(ticket.getAdultCount());
            childrenSpinner.getValueFactory().setValue(ticket.getChildCount());
            bikesSpinner.getValueFactory().setValue(ticket.getBikeCount());
            priceField.setText(ticket.getTotalPrice() != null ? ticket.getTotalPrice().toString() : "");
            if (ticket.getPurchaseDate() != null)
                purchaseDatePicker.setValue(ticket.getPurchaseDate().toLocalDate());
        }
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    @FXML
    private void onSave() {
        try {
            if (userComboBox.getValue() == null || tripComboBox.getValue() == null ||
                    startStopComboBox.getValue() == null || endStopComboBox.getValue() == null ||
                    purchaseDatePicker.getValue() == null) {
                AlertUtil.showError("Fehler", "Bitte alle Felder ausfüllen!");
                return;
            }
            if (ticket == null) {
                ticket = new Ticket(
                        userComboBox.getValue(),
                        tripComboBox.getValue(),
                        startStopComboBox.getValue(),
                        endStopComboBox.getValue(),
                        adultsSpinner.getValue(),
                        childrenSpinner.getValue(),
                        bikesSpinner.getValue(),
                        Double.parseDouble(priceField.getText()),
                        LocalDateTime.of(purchaseDatePicker.getValue(), LocalTime.now())
                );
            } else {
                ticket.setUser(userComboBox.getValue());
                ticket.setTrip(tripComboBox.getValue());
                ticket.setStartStop(startStopComboBox.getValue());
                ticket.setEndStop(endStopComboBox.getValue());
                ticket.setAdultCount(adultsSpinner.getValue());
                ticket.setChildCount(childrenSpinner.getValue());
                ticket.setBikeCount(bikesSpinner.getValue());
                ticket.setTotalPrice(Double.parseDouble(priceField.getText()));
                ticket.setPurchaseDate(LocalDateTime.of(purchaseDatePicker.getValue(), LocalTime.now()));
            }
            new TicketDao().save(ticket);

            if (onSaved != null) onSaved.run();
            close();
        } catch (Exception e) {
            AlertUtil.showError("Fehler beim Speichern", e.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        close();
    }

    private void close() {
        ((Stage) userComboBox.getScene().getWindow()).close();
    }
}
