package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.model.Bus;
import de.fhzwickau.reisewelle.dao.BusDao;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;

public class AdminBusesController {

    @FXML private TableView<Bus> busesTable;
    @FXML private TableColumn<Bus, String> busNumberColumn;
    @FXML private TableColumn<Bus, Integer> totalSeatsColumn;
    @FXML private TableColumn<Bus, String> statusColumn;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private ObservableList<Bus> buses = FXCollections.observableArrayList();
    private BusDao busDao = new BusDao();

    @FXML
    private void initialize() throws SQLException {
        busNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBusNumber()));
        totalSeatsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTotalSeats()).asObject());
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus() != null ? cellData.getValue().getStatus().getName() : ""));

        buses.addAll(busDao.findAll());
        busesTable.setItems(buses);

        busesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            editButton.setDisable(newSelection == null);
            deleteButton.setDisable(newSelection == null);
        });
    }

    @FXML
    private void addBus() {
        showAddEditDialog(null);
    }

    @FXML
    private void editBus() {
        Bus selected = busesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showAddEditDialog(selected);
        }
    }

    @FXML
    private void deleteBus() throws SQLException {
        Bus selected = busesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete this bus?");
            alert.setContentText("Bus Number: " + selected.getBusNumber());
            if (alert.showAndWait().get() == ButtonType.OK) {
                busDao.delete(selected.getId());
                buses.remove(selected);
            }
        }
    }

    private void showAddEditDialog(Bus bus) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/fhzwickau/reisewelle/admin/add-edit-bus.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(bus == null ? "Add Bus" : "Edit Bus");

            AddEditBusController controller = loader.getController();
            controller.setBus(bus);

            stage.setOnHidden(event -> {
                try {
                    buses.setAll(busDao.findAll());
                } catch (SQLException e) {
                    e.printStackTrace();
                    showErrorDialog("Database error", "Could not load updated bus list.");
                }
            });

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}