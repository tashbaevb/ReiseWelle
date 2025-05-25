package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.TripAdminDao;
import de.fhzwickau.reisewelle.model.Trip;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AdminTripController extends BaseTableController<Trip> {

    @FXML private TableView<Trip> tripsTable;
    @FXML private TableColumn<Trip, String> busColumn;
    @FXML private TableColumn<Trip, String> driverColumn;
    @FXML private TableColumn<Trip, String> departureDateColumn;
    @FXML private TableColumn<Trip, String> statusColumn;

    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private final BaseDao<Trip> tripDao = new TripAdminDao();

    @FXML
    protected void initialize() {
        // Configure columns
        busColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().getBus() != null
                                ? cd.getValue().getBus().getBusNumber()
                                : ""
                )
        );
        driverColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().getDriver() != null
                                ? cd.getValue().getDriver().getFirstName()
                                + " " + cd.getValue().getDriver().getLastName()
                                : ""
                )
        );
        departureDateColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().getDepartureDate() != null
                                ? cd.getValue().getDepartureDate().toString()
                                : ""
                )
        );
        statusColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().getStatus() != null
                                ? cd.getValue().getStatus().getName()
                                : ""
                )
        );

        // Disable edit/delete until a row is selected
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        tripsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean disable = (newSel == null);
            editButton.setDisable(disable);
            deleteButton.setDisable(disable);
        });

        loadTripsAsync();
    }

    private void loadTripsAsync() {
        Task<List<Trip>> task = new Task<>() {
            @Override
            protected List<Trip> call() throws Exception {
                return tripDao.findAll();
            }
        };
        task.setOnSucceeded(e -> tripsTable.getItems().setAll(task.getValue()));
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            showError("Error loading trips", ex.getMessage());
        });
        new Thread(task, "LoadTripsThread").start();
    }

    @FXML
    protected void onAdd() {
        try {
            showAddEditDialog(null);
        } catch (IOException ex) {
            showError("Error opening Add dialog", ex.getMessage());
        }
    }

    @FXML
    protected void onEdit() {
        Trip selected = tripsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                showAddEditDialog(selected);
            } catch (IOException ex) {
                showError("Error opening Edit dialog", ex.getMessage());
            }
        }
    }

    @FXML
    protected void onDelete() {
        Trip selected = tripsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Delete trip?");
        confirm.setContentText("Trip ID: " + selected.getId());
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                tripDao.delete(selected.getId());
                loadTripsAsync();
            } catch (SQLException ex) {
                showError("Error deleting trip", ex.getMessage());
            }
        }
    }

    @Override
    protected BaseDao<Trip> getDao() {
        return tripDao;
    }

    @Override
    protected TableView<Trip> getTableView() {
        return tripsTable;
    }

    @Override
    protected Button getEditButton() {
        return editButton;
    }

    @Override
    protected Button getDeleteButton() {
        return deleteButton;
    }

    @Override
    protected UUID getId(Trip trip) {
        return trip.getId();
    }

    @Override
    protected Stage showAddEditDialog(Trip trip) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/de/fhzwickau/reisewelle/admin/add-edit-trip.fxml")
        );
        Parent root = loader.load();
        AddEditTripController controller = loader.getController();
        try {
            controller.setTrip(trip);
        } catch (SQLException ex) {
            showError("Error loading trip data", ex.getMessage());
        }

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(trip == null ? "Add Trip" : "Edit Trip");
        stage.setOnHidden(e -> loadTripsAsync());
        stage.show();
        return stage;
    }

    @Override
    protected String getDeleteConfirmationMessage(Trip trip) {
        return trip.getId().toString();
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
