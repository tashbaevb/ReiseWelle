package de.fhzwickau.reisewelle.controller;

import de.fhzwickau.reisewelle.model.TripSegmentDTO;
import de.fhzwickau.reisewelle.repository.TripRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class TripController implements Initializable {

    @FXML private TextField fromCityField;
    @FXML private TextField toCityField;
    @FXML private DatePicker datePicker;
    @FXML private Spinner<Integer> adultSpinner;
    @FXML private Spinner<Integer> childSpinner;
    @FXML private Spinner<Integer> bikeSpinner;

    @FXML private TableView<TripSegmentDTO> tripTable;
    @FXML private TableColumn<TripSegmentDTO, String> fromColumn;
    @FXML private TableColumn<TripSegmentDTO, String> toColumn;
    @FXML private TableColumn<TripSegmentDTO, LocalDateTime> departureColumn;
    @FXML private TableColumn<TripSegmentDTO, LocalDateTime> arrivalColumn;
    @FXML private TableColumn<TripSegmentDTO, Double> priceColumn;
    @FXML private TableColumn<TripSegmentDTO, Integer> seatsColumn;

    private final TripRepository tripRepo = new TripRepository();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("fromCity"));
        toColumn.setCellValueFactory(new PropertyValueFactory<>("toCity"));
        departureColumn.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        arrivalColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        seatsColumn.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));

        adultSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 1));
        childSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0));
        bikeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0));
    }

    @FXML
    public void onSearchClicked() {
        String from = fromCityField.getText();
        String to = toCityField.getText();
        LocalDate date = datePicker.getValue();

        int adults = adultSpinner.getValue();
        int children = childSpinner.getValue();
        int bikes = bikeSpinner.getValue();

        List<TripSegmentDTO> trips = tripRepo.searchTrips(from, to, date, adults, children, bikes);
        tripTable.getItems().setAll(trips);
    }

    @FXML
    public void onTripSelected(MouseEvent event) throws IOException {
        TripSegmentDTO selected = tripTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/fhzwickau/reisewelle/user/detail_trip.fxml"));
            Parent root = loader.load();
            DetailTripController controller = loader.getController();
            controller.loadTripDetails(selected.getTripId());
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        }
    }
}
