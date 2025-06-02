package de.fhzwickau.reisewelle.controller.admin.trip_status;

import de.fhzwickau.reisewelle.dao.TripStatusDao;
import de.fhzwickau.reisewelle.model.TripStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdminTripStatusController {

    @FXML
    private TableView<TripStatus> tripStatusTable;
    @FXML
    private TableColumn<TripStatus, String> nameColumn;
    @FXML
    private Button addButton, editButton, deleteButton;

    private final TripStatusDao tripStatusDao = new TripStatusDao();
    private final ObservableList<TripStatus> statusList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));
        loadTripStatuses();
        tripStatusTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            editButton.setDisable(newVal == null);
            deleteButton.setDisable(newVal == null);
        });
    }

    private void loadTripStatuses() {
        try {
            statusList.setAll(tripStatusDao.findAll());
            tripStatusTable.setItems(statusList);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Fehler beim Laden der Status: " + e.getMessage());
        }
    }

    @FXML
    public void onAdd() {
        AddEditTripStatusController.showDialog(null, v -> loadTripStatuses());
    }

    @FXML
    public void onEdit() {
        TripStatus selected = tripStatusTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            AddEditTripStatusController.showDialog(selected, v -> loadTripStatuses());
        }
    }

    @FXML
    public void onDelete() {
        TripStatus selected = tripStatusTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Status wirklich löschen?", ButtonType.YES, ButtonType.NO);
            alert.setHeaderText(null);
            alert.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.YES) {
                    try {
                        tripStatusDao.delete(selected.getId());
                        loadTripStatuses();
                    } catch (Exception e) {
                        showError("Fehler beim Löschen: " + e.getMessage());
                    }
                }
            });
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText("Fehler");
        a.showAndWait();
    }
}
