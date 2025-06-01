package de.fhzwickau.reisewelle.controller.admin.trip;

import de.fhzwickau.reisewelle.controller.admin.BaseTableController;
import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.TripAdminDao;
import de.fhzwickau.reisewelle.model.Trip;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import de.fhzwickau.reisewelle.utils.WindowUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
        return WindowUtil.showModalWindow(
                "/de/fhzwickau/reisewelle/admin/trip/add-edit-trip.fxml",
                trip == null ? "Reise hinzufügen" : "Reise bearbeiten",
                controller -> {
                    try {
                        ((AddEditTripController) controller).setTrip(trip);
                    } catch (SQLException sqle) {
                        AlertUtil.showError("Fehler beim Laden der Reisedaten", sqle.getMessage());
                    }
                },
                this::loadDataAsync
        );
    }

    @Override
    protected String getDeleteConfirmationMessage(Trip trip) {
        return "Reise ID: " + trip.getId();
    }

    @Override
    protected TableView<Trip> getTableView() {
        return tripsTable;
    }
}
