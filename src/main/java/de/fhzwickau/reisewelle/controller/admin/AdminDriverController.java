package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.DriverDao;
import de.fhzwickau.reisewelle.dao.TripAdminDao;
import de.fhzwickau.reisewelle.model.Driver;
import de.fhzwickau.reisewelle.model.Trip;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
 * Controller for Driver administration table.
 */
public class AdminDriverController extends BaseTableController<Driver> {

    @FXML private TableView<Driver> driversTable;
    @FXML private TableColumn<Driver, String> firstNameColumn;
    @FXML private TableColumn<Driver, String> lastNameColumn;
    @FXML private TableColumn<Driver, String> licenseNumberColumn;
    @FXML private TableColumn<Driver, String> statusColumn;

    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private final BaseDao<Driver> driverDao     = new DriverDao();
    private final TripAdminDao     tripDao       = new TripAdminDao();

    @FXML
    protected void initialize() {
        // Configure table columns
        firstNameColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getFirstName())
        );
        lastNameColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getLastName())
        );
        licenseNumberColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getLicenseNumber())
        );
        statusColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getStatus().getName())
        );

        // Disable Edit/Delete until a row is selected
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
        task.setOnFailed(e -> showError("Error loading drivers", task.getException().getMessage()));
        new Thread(task, "LoadDriversThread").start();
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
        Driver selected = driversTable.getSelectionModel().getSelectedItem();
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
        Driver selected = driversTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        // Prevent deletion if driver is assigned to any trips
        try {
            boolean inUse = tripDao.findAll().stream()
                    .anyMatch(trip -> trip.getDriver().getId().equals(selected.getId()));
            if (inUse) {
                showError("Cannot delete driver",
                        "This driver is assigned to one or more trips.\n" +
                                "Please reassign or delete those trips first.");
                return;
            }
        } catch (SQLException ex) {
            showError("Error checking trips", ex.getMessage());
            return;
        }

        // Confirm deletion
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Delete driver?");
        confirm.setContentText("Driver License Number: " + selected.getLicenseNumber());
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                driverDao.delete(selected.getId());
                loadDriversAsync();
            } catch (SQLException ex) {
                showError("Error deleting driver", ex.getMessage());
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
                getClass().getResource("/de/fhzwickau/reisewelle/admin/add-edit-driver.fxml")
        );
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(driver == null ? "Add Driver" : "Edit Driver");

        AddEditDriverController controller = loader.getController();
        controller.setDriver(driver);

        stage.setOnHidden(event -> loadDriversAsync());
        stage.show();
        return stage;
    }

    @Override
    protected String getDeleteConfirmationMessage(Driver driver) {
        return "Driver License Number: " + driver.getLicenseNumber();
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
