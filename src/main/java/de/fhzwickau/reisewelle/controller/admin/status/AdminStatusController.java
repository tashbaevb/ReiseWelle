package de.fhzwickau.reisewelle.controller.admin.status;

import de.fhzwickau.reisewelle.dao.StatusDao;
import de.fhzwickau.reisewelle.model.Status;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.function.Consumer;

public class AdminStatusController {

    @FXML
    private TableView<Status> statusTable;
    @FXML
    private TableColumn<Status, String> nameColumn;
    @FXML
    private Button addButton, editButton, deleteButton;

    private final StatusDao statusDao = new StatusDao();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));
        loadStatuses();
        statusTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            editButton.setDisable(newVal == null);
            deleteButton.setDisable(newVal == null);
        });
    }

    private void loadStatuses() {
        try {
            statusTable.setItems(FXCollections.observableArrayList(statusDao.findAll()));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Fehler beim Laden der Status: " + e.getMessage());
        }
    }

    @FXML
    public void onAdd() {
        AddEditStatusController.showDialog(null, v -> loadStatuses());
    }

    @FXML
    public void onEdit() {
        Status selected = statusTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            AddEditStatusController.showDialog(selected, v -> loadStatuses());
        }
    }

    @FXML
    public void onDelete() {
        Status selected = statusTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Status wirklich löschen?", ButtonType.YES, ButtonType.NO);
            alert.setHeaderText(null);
            alert.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.YES) {
                    try {
                        statusDao.delete(selected.getId());
                        loadStatuses();
                    } catch (Exception e) {
                        e.printStackTrace();
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
