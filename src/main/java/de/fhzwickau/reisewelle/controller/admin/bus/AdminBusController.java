package de.fhzwickau.reisewelle.controller.admin.bus;

import de.fhzwickau.reisewelle.controller.admin.BaseTableController;
import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.BusDao;
import de.fhzwickau.reisewelle.dao.TripAdminDao;
import de.fhzwickau.reisewelle.model.Bus;
import de.fhzwickau.reisewelle.model.Trip;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
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

        init(busDao, busesTable, editButton, deleteButton);
    }

    @Override
    protected boolean isInUse(Bus bus) {
        try {
            return tripDao.findAll().stream().anyMatch(trip -> trip.getBus().getId().equals(bus.getId()));
        } catch (SQLException e) {
            showError("Fehler bei Verwendungskontrolle", e.getMessage());
            return true;
        }
    }

    @Override
    protected String getInUseMessage() {
        return "Dieser Bus ist einer oder mehreren Fahrten zugewiesen. Bitte Fahrten zuerst neu zuweisen oder löschen.";
    }

    @Override
    protected UUID getId(Bus bus) {
        return bus.getId();
    }

    @Override
    protected String getDeleteConfirmationMessage(Bus bus) {
        return "Bus Nummer: " + bus.getBusNumber();
    }

    @Override
    protected Stage showAddEditDialog(Bus bus) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/fhzwickau/reisewelle/admin/bus/add-edit-bus.fxml"));
        Parent root = loader.load();
        AddEditBusController controller = loader.getController();
        controller.setBus(bus);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(bus == null ? "Bus hinzufügen" : "Bus bearbeiten");
        stage.setOnHidden(e -> loadDataAsync());
        stage.show();
        return stage;
    }

    @Override
    protected TableView<Bus> getTableView() {
        return busesTable;
    }
}
