package de.fhzwickau.reisewelle.controller.admin.trip_status;

import de.fhzwickau.reisewelle.controller.admin.BaseTableController;
import de.fhzwickau.reisewelle.dao.TripAdminDao;
import de.fhzwickau.reisewelle.dao.TripDao;
import de.fhzwickau.reisewelle.dao.TripStatusDao;
import de.fhzwickau.reisewelle.model.TripStatus;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class AdminTripStatusController extends BaseTableController<TripStatus> {

    @FXML
    private TableView<TripStatus> tripStatusTable;
    @FXML
    private TableColumn<TripStatus, String> nameColumn;
    @FXML
    private Button addButton, editButton, deleteButton;

    private final TripStatusDao tripStatusDao = new TripStatusDao();
    private final TripAdminDao tripDao = new TripAdminDao();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        init(tripStatusDao, tripStatusTable, editButton, deleteButton);

        addButton.setOnAction(e -> onAdd());
    }

    @Override
    protected boolean isInUse(TripStatus status) {
        try {
            return tripDao.isTripWithStatusId(status.getId());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return true;
        }
    }

    @Override
    protected String getInUseMessage() {
        return "Der Status wird verwendet und kann nicht gelöscht werden.";
    }

    @Override
    protected UUID getId(TripStatus entity) {
        return entity.getId();
    }

    @Override
    protected String getDeleteConfirmationMessage(TripStatus entity) {
        return "Status: " + entity.getName() + " wirklich löschen?";
    }

    @Override
    protected Stage showAddEditDialog(TripStatus entity) throws IOException {
        return de.fhzwickau.reisewelle.utils.WindowUtil.showModalWindow(
                "/de/fhzwickau/reisewelle/admin/trip_status/add-edit-trip-status.fxml",
                entity == null ? "Status hinzufügen" : "Status bearbeiten",
                controller -> {
                    AddEditTripStatusController c = (AddEditTripStatusController) controller;
                    c.setTripStatus(entity);
                    c.setOnSaved(this::loadDataAsync);
                },
                this::loadDataAsync
        );
    }

    @Override
    protected TableView<TripStatus> getTableView() {
        return tripStatusTable;
    }

    @Override
    @FXML
    protected void onDelete() {
        try {
            super.onDelete();
        } catch (SQLException e) {
            AlertUtil.showError("Fehler beim Löschen", e.getMessage());
        }
    }
}
