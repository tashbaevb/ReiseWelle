package de.fhzwickau.reisewelle.controller.user;

import de.fhzwickau.reisewelle.dto.TripSegmentDTO;
import de.fhzwickau.reisewelle.dao.TripDao;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import de.fhzwickau.reisewelle.utils.CustomTimePickerDialog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class TripController implements Initializable {

    @FXML private TextField fromCityField, toCityField;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private Spinner<Integer> adultSpinner, childSpinner, bikeSpinner;

    @FXML private TableView<TripSegmentDTO> tripTable;
    @FXML private TableColumn<TripSegmentDTO, String> fromColumn, toColumn;
    @FXML private TableColumn<TripSegmentDTO, LocalDateTime> departureColumn, arrivalColumn;
    @FXML private TableColumn<TripSegmentDTO, Double> priceColumn;

    private final TripDao tripDao = new TripDao();
    private LocalTime pickedTime = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("fromCity"));
        toColumn.setCellValueFactory(new PropertyValueFactory<>("toCity"));
        departureColumn.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        arrivalColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        adultSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 40, 1));
        childSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 40, 0));
        bikeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        departureColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.format(formatter));
            }
        });

        arrivalColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.format(formatter));
            }
        });

        tripTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void onPickTime() {
        CustomTimePickerDialog.showAndWait().ifPresent(time -> {
            pickedTime = time;
            timeField.setText(time.format(DateTimeFormatter.ofPattern("HH:mm")));
        });
    }

    @FXML
    public void onSearchClicked() throws SQLException {
        try {
            String from = fromCityField.getText();
            String to = toCityField.getText();

            LocalDate date = datePicker.getValue();
            LocalTime time = pickedTime;
            LocalDateTime dateTime = null;
            if (date != null && time != null) {
                dateTime = LocalDateTime.of(date, time);
            }

            int adults = adultSpinner.getValue();
            int children = childSpinner.getValue();
            int bikes = bikeSpinner.getValue();

            List<TripSegmentDTO> trips = tripDao.searchTrips(from, to, dateTime, adults, children, bikes);
            tripTable.getItems().setAll(trips);
        } catch (SQLException sqle) {
            AlertUtil.showError("Fehler bei der Suche", sqle.getMessage());
        }
    }

    @FXML
    public void onTripSelected() throws IOException, SQLException {
        TripSegmentDTO selected = tripTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/fhzwickau/reisewelle/user/detail_trip.fxml"));
            Parent root = loader.load();

            DetailTripController controller = loader.getController();
            controller.setViewingTicket(false);

            controller.loadTripDetails(
                    selected.getTripId(),
                    selected.getPrice(),
                    selected.getStartStopId().toString(),
                    selected.getEndStopId().toString(),
                    adultSpinner.getValue(),
                    childSpinner.getValue(),
                    bikeSpinner.getValue()
            );

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        }
    }
}
