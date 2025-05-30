package de.fhzwickau.reisewelle.controller.admin.driver;

import de.fhzwickau.reisewelle.controller.admin.BaseTableController;
import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.DriverDao;
import de.fhzwickau.reisewelle.dao.TripAdminDao;
import de.fhzwickau.reisewelle.model.Driver;
import de.fhzwickau.reisewelle.model.Trip;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
 * Controller for Driver administration table.
 */
public class AdminDriverController extends BaseTableController<Driver> {

    @FXML
    private TableView<Driver> driversTable;
    @FXML
    private TableColumn<Driver, String> firstNameColumn, lastNameColumn, licenseNumberColumn, statusColumn;
    @FXML
    private Button editButton, deleteButton;

    private final BaseDao<Driver> driverDao = new DriverDao();
    private final BaseDao<Trip> tripDao = new TripAdminDao();

    @FXML
    protected void initialize() {
        firstNameColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getFirstName()));
        lastNameColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getLastName()));
        licenseNumberColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getLicenseNumber()));
        statusColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStatus().getName()));

        editButton.setDisable(true);
        deleteButton.setDisable(true);
        driversTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    boolean disable = newSel == null;
                    editButton.setDisable(disable);
                    deleteButton.setDisable(disable);
                });

        loadDriversAsync();
    }

    private void loadDriversAsync() {
        Task<List<Driver>> task = new Task<>() {
            @Override
            protected List<Driver> call() throws Exception {
                return driverDao.findAll();
            }
        };
        task.setOnSucceeded(e -> driversTable.getItems().setAll(task.getValue()));
        task.setOnFailed(e -> showError("Fehler beim Laden der Fahrer", task.getException().getMessage()));
        new Thread(task, "LoadDriversThread").start();
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
        Driver selected = driversTable.getSelectionModel().getSelectedItem();
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
        Driver selected = driversTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            boolean inUse = tripDao.findAll().stream().anyMatch(trip -> trip.getDriver().getId().equals(selected.getId()));
            if (inUse) {
                showError("Fahrer kann nicht gelöscht werden",
                        "Der Fahrer ist einer oder mehreren Fahrten zugewiesen. Bitte weisen Sie diese Fahrten zuerst neu zu oder löschen Sie sie.");
                return;
            }
        } catch (SQLException sqle) {
            showError("Fehlerprüfung", sqle.getMessage());
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Den Fahrer löschen?");
        confirm.setContentText("Fahrer: " + selected.getLicenseNumber());
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                driverDao.delete(selected.getId());
                loadDriversAsync();
            } catch (SQLException sqle) {
                showError("Fehler beim Löschen des Fahrers", sqle.getMessage());
            }
        }
    }

    @Override
    protected BaseDao<Driver> getDao() {
        return driverDao;
    }

    @Override
    protected TableView<Driver> getTableView() {
        return driversTable;
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
    protected UUID getId(Driver driver) {
        return driver.getId();
    }

    @Override
    protected Stage showAddEditDialog(Driver driver) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/de/fhzwickau/reisewelle/admin/driver/add-edit-driver.fxml")
        );
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(driver == null ? "Den Fahrer hinzufügen" : "Den Fahrer bearbeiten");

        AddEditDriverController controller = loader.getController();
        controller.setDriver(driver);

        stage.setOnHidden(event -> loadDriversAsync());
        stage.show();
        return stage;
    }

    @Override
    protected String getDeleteConfirmationMessage(Driver driver) {
        return "Führerscheinnummer: " + driver.getLicenseNumber();
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
