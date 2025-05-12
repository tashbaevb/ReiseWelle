package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.model.Trip;
import de.fhzwickau.reisewelle.repository.TripAdminRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminTripsController {

    @FXML private TableView<Trip> tripsTable;
    @FXML private TableColumn<Trip, String> busColumn;
    @FXML private TableColumn<Trip, String> driverColumn;
    @FXML private TableColumn<Trip, String> departureDateColumn;
    @FXML private TableColumn<Trip, String> statusColumn;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private ObservableList<Trip> trips = FXCollections.observableArrayList();
    private TripAdminRepository tripRepository = new TripAdminRepository();

    @FXML
    private void initialize() {
        busColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBus() != null ? cellData.getValue().getBus().getBusNumber() : ""));
        driverColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDriver() != null ? cellData.getValue().getDriver().getFirstName() + " " + cellData.getValue().getDriver().getLastName() : ""));
        departureDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartureDate().toString()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().getName()));

        trips.addAll(tripRepository.findAll());
        tripsTable.setItems(trips);

        tripsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            editButton.setDisable(newSelection == null);
            deleteButton.setDisable(newSelection == null);
        });
    }

    @FXML
    private void addTrip() {
        showAddEditDialog(null);
    }

    @FXML
    private void editTrip() {
        Trip selected = tripsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showAddEditDialog(selected);
        }
    }

    @FXML
    private void deleteTrip() {
        Trip selected = tripsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete this trip?");
            alert.setContentText("Departure Date: " + selected.getDepartureDate());
            if (alert.showAndWait().get() == ButtonType.OK) {
                tripRepository.delete(selected.getId());
                trips.remove(selected);
            }
        }
    }

    private void showAddEditDialog(Trip trip) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/fhzwickau/reisewelle/admin/add-edit-trip.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(trip == null ? "Add Trip" : "Edit Trip");

            AddEditTripController controller = loader.getController();
            controller.setTrip(trip);

            stage.setOnHidden(event -> trips.setAll(tripRepository.findAll()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}