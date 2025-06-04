package de.fhzwickau.reisewelle.controller.admin.ticket;

import de.fhzwickau.reisewelle.controller.admin.BaseAddEditController;
import de.fhzwickau.reisewelle.dao.StopDao;
import de.fhzwickau.reisewelle.dao.TicketDao;
import de.fhzwickau.reisewelle.dao.TripAdminDao;
import de.fhzwickau.reisewelle.dao.UserDao;
import de.fhzwickau.reisewelle.model.Stop;
import de.fhzwickau.reisewelle.model.Ticket;
import de.fhzwickau.reisewelle.model.Trip;
import de.fhzwickau.reisewelle.model.User;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class AddEditTicketController extends BaseAddEditController<Ticket> {

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

    private final UserDao userDao = new UserDao();
    private final TripAdminDao tripDao = new TripAdminDao();
    private final StopDao stopDao = new StopDao();
    private final TicketDao ticketDao = new TicketDao();

    private Runnable onSaved;

    @FXML
    public void initialize() {
        try {
            List<User> users = userDao.findAll();
            userComboBox.setItems(FXCollections.observableList(users));

            List<Trip> trips = tripDao.findAll();
            tripComboBox.setItems(FXCollections.observableList(trips));

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
                List<Stop> stops = stopDao.findByTripId(selectedTrip.getId());
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
        this.entity = ticket;
        if (entity != null) {
            userComboBox.setValue(entity.getUser());
            tripComboBox.setValue(entity.getTrip());

            if (entity.getTrip() != null) {
                updateStops();
                startStopComboBox.setValue(entity.getStartStop());
                endStopComboBox.setValue(entity.getEndStop());
            }
            adultsSpinner.getValueFactory().setValue(entity.getAdultCount());
            childrenSpinner.getValueFactory().setValue(entity.getChildCount());
            bikesSpinner.getValueFactory().setValue(entity.getBikeCount());
            priceField.setText(entity.getTotalPrice() != null ? entity.getTotalPrice().toString() : "");
            if (entity.getPurchaseDate() != null)
                purchaseDatePicker.setValue(entity.getPurchaseDate().toLocalDate());
        }
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    @Override
    protected void saveEntity() throws SQLException {
        if (userComboBox.getValue() == null || tripComboBox.getValue() == null ||
                startStopComboBox.getValue() == null || endStopComboBox.getValue() == null ||
                purchaseDatePicker.getValue() == null) {
            throw new IllegalArgumentException("Bitte alle Felder ausfüllen!");
        }

        if (entity == null) {
            entity = new Ticket(
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
            entity.setUser(userComboBox.getValue());
            entity.setTrip(tripComboBox.getValue());
            entity.setStartStop(startStopComboBox.getValue());
            entity.setEndStop(endStopComboBox.getValue());
            entity.setAdultCount(adultsSpinner.getValue());
            entity.setChildCount(childrenSpinner.getValue());
            entity.setBikeCount(bikesSpinner.getValue());
            entity.setTotalPrice(Double.parseDouble(priceField.getText()));
            entity.setPurchaseDate(LocalDateTime.of(purchaseDatePicker.getValue(), LocalTime.now()));
        }

        ticketDao.save(entity);

        if (onSaved != null) {
            onSaved.run();
        }
    }

    @Override
    protected Node getAnyControl() {
        return userComboBox;
    }
}
