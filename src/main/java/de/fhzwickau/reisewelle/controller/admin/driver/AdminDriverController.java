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

        // Инициализация базового контроллера: dao, таблица, кнопки
        init(driverDao, driversTable, editButton, deleteButton);

        // Загрузка данных асинхронно через переопределённый метод
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
            showError("Fehler beim Laden der Fahrer", ex.getMessage());
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
            showError("Fehlerprüfung", e.getMessage());
            // Если не можем проверить, безопаснее запретить удаление
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/fhzwickau/reisewelle/admin/driver/add-edit-driver.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(driver == null ? "Den Fahrer hinzufügen" : "Den Fahrer bearbeiten");

        AddEditDriverController controller = loader.getController();
        controller.setDriver(driver);

        stage.setOnHidden(event -> loadDataAsync());
        stage.show();
        return stage;
    }

    @Override
    protected String getDeleteConfirmationMessage(Driver driver) {
        return "Führerscheinnummer: " + driver.getLicenseNumber();
    }

    public void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    protected TableView<Driver> getTableView() {
        return driversTable;
    }
}
