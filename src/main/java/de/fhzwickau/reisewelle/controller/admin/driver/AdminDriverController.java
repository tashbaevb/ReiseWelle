package de.fhzwickau.reisewelle.controller.admin.driver;

import de.fhzwickau.reisewelle.controller.admin.BaseTableController;
import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.DriverDao;
import de.fhzwickau.reisewelle.dao.TripAdminDao;
import de.fhzwickau.reisewelle.model.Driver;
import de.fhzwickau.reisewelle.model.Trip;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import de.fhzwickau.reisewelle.utils.WindowUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

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

        init(driverDao, driversTable, editButton, deleteButton);
        loadDataAsync();
    }

    @Override
    protected void loadDataAsync() {
        Task<List<Driver>> task = new Task<>() {
            @Override
            protected List<Driver> call() throws Exception {
                return driverDao.findAll();
            }
        };
        task.setOnSucceeded(e -> getTableView().getItems().setAll(task.getValue()));
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            AlertUtil.showError("Fehler beim Laden der Fahrer", ex.getMessage());
        });
        new Thread(task, "LoadDriversThread").start();
    }

    @Override
    protected boolean isInUse(Driver driver) {
        try {
            List<Trip> trips = tripDao.findAll();
            return trips.stream()
                    .anyMatch(trip -> trip.getDriver() != null && trip.getDriver().getId().equals(driver.getId()));
        } catch (SQLException e) {
            AlertUtil.showError("Fehlerprüfung", e.getMessage());
            return true;
        }
    }

    @Override
    protected String getInUseMessage() {
        return "Der Fahrer ist einer oder mehreren Fahrten zugewiesen. Bitte weisen Sie diese Fahrten zuerst neu zu oder löschen Sie sie.";
    }

    @Override
    protected UUID getId(Driver driver) {
        return driver.getId();
    }

    @Override
    protected Stage showAddEditDialog(Driver driver) throws IOException {
        return WindowUtil.showModalWindow(
                "/de/fhzwickau/reisewelle/admin/driver/add-edit-driver.fxml",
                driver == null ? "Den Fahrer hinzufügen" : "Den Fahrer bearbeiten",
                controller -> ((AddEditDriverController) controller).setDriver(driver),
                this::loadDataAsync
        );
    }

    @Override
    protected String getDeleteConfirmationMessage(Driver driver) {
        return "Führerscheinnummer: " + driver.getLicenseNumber();
    }

    @Override
    protected TableView<Driver> getTableView() {
        return driversTable;
    }
}
