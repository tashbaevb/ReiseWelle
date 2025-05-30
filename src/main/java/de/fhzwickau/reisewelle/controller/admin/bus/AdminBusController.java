package de.fhzwickau.reisewelle.controller.admin.bus;

import de.fhzwickau.reisewelle.controller.admin.BaseTableController;
import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.BusDao;
import de.fhzwickau.reisewelle.dao.TripAdminDao;
import de.fhzwickau.reisewelle.model.Bus;
import de.fhzwickau.reisewelle.model.Trip;
import javafx.beans.property.ReadOnlyObjectWrapper;
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

/**
 * Controller for Bus administration table
 */
public class AdminBusController extends BaseTableController<Bus> {

    @FXML
    private TableView<Bus> busesTable;
    @FXML
    private TableColumn<Bus, String> busNumberColumn, statusColumn;
    @FXML
    private TableColumn<Bus, Integer> totalSeatsColumn, bikeSpacesColumn;

    @FXML
    private Button editButton, deleteButton;

    private final BaseDao<Bus> busDao = new BusDao();
    private final BaseDao<Trip> tripDao = new TripAdminDao();

    @FXML
    protected void initialize() {
        busNumberColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getBusNumber()));
        totalSeatsColumn.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getTotalSeats()));
        bikeSpacesColumn.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getBicycleSpaces()));
        statusColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStatus().getName()));

        editButton.setDisable(true);
        deleteButton.setDisable(true);
        busesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean disable = (newSel == null);
            editButton.setDisable(disable);
            deleteButton.setDisable(disable);
        });

        loadBusesAsync();
    }

    private void loadBusesAsync() {
        Task<List<Bus>> task = new Task<>() {
            @Override
            protected List<Bus> call() throws Exception {
                return busDao.findAll();
            }
        };
        task.setOnSucceeded(e -> busesTable.getItems().setAll(task.getValue()));
        task.setOnFailed(e -> showError("Fehler beim Laden der Busse", task.getException().getMessage()));
        new Thread(task, "LoadBusesThread").start();
    }

    @FXML
    protected void onAdd() {
        try {
            showAddEditDialog(null);
        } catch (IOException ioe) {
            showError("Fehler beim Öffnen des Dialogfelds „Hinzufügen“", ioe.getMessage());
        }
    }

    @FXML
    protected void onEdit() {
        Bus selected = busesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                showAddEditDialog(selected);
            } catch (IOException ioe) {
                showError("Fehler beim Öffnen des Dialogfelds „Ändern“", ioe.getMessage());
            }
        }
    }

    @FXML
    protected void onDelete() {
        Bus selected = busesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            boolean inUse = tripDao.findAll().stream().anyMatch(trip -> trip.getBus().getId().equals(selected.getId()));
            if (inUse) {
                showError("Bus kann nicht gelöscht werden",
                        "Dieser Bus ist einer oder mehreren Fahrten zugewiesen. Bitte weisen Sie diese Fahrten zuerst neu zu oder löschen Sie sie.");
                return;
            }
        } catch (SQLException sqle) {
            showError("Fehlerprüfung", sqle.getMessage());
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Das Bus löschen?");
        confirm.setContentText("Bus Nummer: " + selected.getBusNumber());
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                busDao.delete(selected.getId());
                loadBusesAsync();
            } catch (SQLException ex) {
                showError("Fehler beim Löschen", ex.getMessage());
            }
        }
    }

    @Override
    protected BaseDao<Bus> getDao() {
        return busDao;
    }

    @Override
    protected TableView<Bus> getTableView() {
        return busesTable;
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
    protected UUID getId(Bus bus) {
        return bus.getId();
    }

    @Override
    protected Stage showAddEditDialog(Bus bus) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/de/fhzwickau/reisewelle/admin/bus/add-edit-bus.fxml")
        );
        Parent root = loader.load();
        AddEditBusController controller = loader.getController();
        controller.setBus(bus);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(bus == null ? "Bus hinzufügen" : "Bus bearbeiten");
        stage.setOnHidden(e -> loadBusesAsync());
        stage.show();
        return stage;
    }

    @Override
    protected String getDeleteConfirmationMessage(Bus bus) {
        return bus.getBusNumber().toString();
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
