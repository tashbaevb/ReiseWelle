package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.BusDao;
import de.fhzwickau.reisewelle.model.Bus;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
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

public class AdminBusController extends BaseTableController<Bus> {

    @FXML private TableView<Bus>          busesTable;
    @FXML private TableColumn<Bus, String> busNumberColumn;
    @FXML private TableColumn<Bus, Integer> totalSeatsColumn;
    @FXML private TableColumn<Bus, Integer> bikeSpacesColumn;
    @FXML private TableColumn<Bus, String> statusColumn;

    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private final BaseDao<Bus> busDao = new BusDao();

    @FXML
    protected void initialize() {
        // configure columns
        busNumberColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getBusNumber())
        );
        totalSeatsColumn.setCellValueFactory(cd ->
                new ReadOnlyObjectWrapper<>(cd.getValue().getTotalSeats())
        );
        bikeSpacesColumn.setCellValueFactory(cd ->
                new ReadOnlyObjectWrapper<>(cd.getValue().getBikeSpaces())
        );
        statusColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getStatus().getName())
        );

        // disable edit/delete until a row is selected
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        busesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean disable = (newSel == null);
            editButton.setDisable(disable);
            deleteButton.setDisable(disable);
        });

        loadBusesAsync();
    }

    private void loadBusesAsync() {
        Task<List<Bus>> task = new Task<>() {
            @Override
            protected List<Bus> call() throws Exception {
                return busDao.findAll();
            }
        };
        task.setOnSucceeded(e -> busesTable.getItems().setAll(task.getValue()));
        task.setOnFailed(e -> showError("Error loading buses", task.getException().getMessage()));
        new Thread(task, "LoadBusesThread").start();
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
        Bus selected = busesTable.getSelectionModel().getSelectedItem();
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
        Bus selected = busesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Delete bus?");
        confirm.setContentText("ID: " + selected.getId());
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                busDao.delete(selected.getId());
                loadBusesAsync();
            } catch (SQLException ex) {
                showError("Error deleting bus", ex.getMessage());
            }
        }
    }

    @Override
    protected BaseDao<Bus> getDao() {
        return busDao;
    }

    @Override
    protected TableView<Bus> getTableView() {
        return busesTable;
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
    protected UUID getId(Bus bus) {
        return bus.getId();
    }

    @Override
    protected Stage showAddEditDialog(Bus bus) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/de/fhzwickau/reisewelle/admin/add-edit-bus.fxml")
        );
        Parent root = loader.load();
        AddEditBusController controller = loader.getController();
        controller.setBus(bus);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(bus == null ? "Add Bus" : "Edit Bus");
        stage.setOnHidden(e -> loadBusesAsync());
        stage.show();
        return stage;
    }

    @Override
    protected String getDeleteConfirmationMessage(Bus bus) {
        return bus.getId().toString();
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
