package de.fhzwickau.reisewelle.controller.admin.trip;

import de.fhzwickau.reisewelle.controller.admin.BaseTableController;
import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.TripAdminDao;
import de.fhzwickau.reisewelle.model.Trip;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class AdminTripController extends BaseTableController<Trip> {

    @FXML
    private TableView<Trip> tripsTable;
    @FXML
    private TableColumn<Trip, String> busColumn, driverColumn, departureDateColumn, statusColumn;
    @FXML
    private Button editButton, deleteButton;

    private final BaseDao<Trip> tripDao = new TripAdminDao();

    @FXML
    protected void initialize() {
        busColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getBus() != null ? cd.getValue().getBus().getBusNumber() : "")
        );
        driverColumn.setCellValueFactory(cd -> {
            if (cd.getValue().getDriver() != null) {
                return new SimpleStringProperty(cd.getValue().getDriver().getFirstName() + " " + cd.getValue().getDriver().getLastName());
            } else {
                return new SimpleStringProperty("");
            }
        });
        departureDateColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getDepartureDate() != null ? cd.getValue().getDepartureDate().toString() : "")
        );
        statusColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getStatus() != null ? cd.getValue().getStatus().getName() : "")
        );

        init(tripDao, tripsTable, editButton, deleteButton);
    }

    @Override
    protected boolean isInUse(Trip trip) {
        // Если есть зависимости, например, билеты или что-то еще — реализуй здесь проверку.
        // Иначе просто false.
        return false;
    }

    @Override
    protected String getInUseMessage() {
        return "Diese Reise kann nicht gelöscht werden, da sie in Verwendung ist.";
    }

    @Override
    protected UUID getId(Trip trip) {
        return trip.getId();
    }

    @Override
    protected Stage showAddEditDialog(Trip trip) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/fhzwickau/reisewelle/admin/trip/add-edit-trip.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(trip == null ? "Reise hinzufügen" : "Reise bearbeiten");

        AddEditTripController controller = loader.getController();
        try {
            controller.setTrip(trip);
        } catch (SQLException ex) {
            showError("Fehler beim Laden der Reisedaten", ex.getMessage());
        }

        stage.setOnHidden(e -> loadDataAsync());
        stage.show();
        return stage;
    }

    @Override
    protected String getDeleteConfirmationMessage(Trip trip) {
        return "Reise ID: " + trip.getId();
    }

    public void showError(String header, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    protected TableView<Trip> getTableView() {
        return tripsTable;
    }
}
