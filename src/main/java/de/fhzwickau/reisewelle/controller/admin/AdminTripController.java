package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.model.Trip;
import de.fhzwickau.reisewelle.dao.TripAdminDao;
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
    private TableColumn<Trip, String> busColumn;
    @FXML
    private TableColumn<Trip, String> driverColumn;
    @FXML
    private TableColumn<Trip, String> departureDateColumn;
    @FXML
    private TableColumn<Trip, String> statusColumn;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    private final BaseDao<Trip> tripDao = new TripAdminDao();

    @FXML
    protected void initialize() throws SQLException {
        busColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBus() != null ? cellData.getValue().getBus().getBusNumber() : ""));
        driverColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDriver() != null ? cellData.getValue().getDriver().getFirstName() + " " + cellData.getValue().getDriver().getLastName() : ""));
        departureDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartureDate().toString()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().getName()));
        super.initialize();
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/fhzwickau/reisewelle/admin/add-edit-trip.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(trip == null ? "Add Trip" : "Edit Trip");

        AddEditTripController controller = loader.getController();
        controller.setTrip(trip);

        stage.setOnHidden(event -> refreshData());
        stage.show();

        return stage;
    }

    @Override
    protected String getDeleteConfirmationMessage(Trip trip) {
        return trip.getId().toString();
    }
}