package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.model.Driver;
import de.fhzwickau.reisewelle.repository.DriverRepository;
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

public class AdminDriversController {

    @FXML private TableView<Driver> driversTable;
    @FXML private TableColumn<Driver, String> firstNameColumn;
    @FXML private TableColumn<Driver, String> lastNameColumn;
    @FXML private TableColumn<Driver, String> licenseNumberColumn;
    @FXML private TableColumn<Driver, String> statusColumn;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private ObservableList<Driver> drivers = FXCollections.observableArrayList();
    private DriverRepository driverRepository = new DriverRepository();

    @FXML
    private void initialize() {
        firstNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName()));
        lastNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));
        licenseNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLicenseNumber()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().getName()));

        drivers.addAll(driverRepository.findAll());
        driversTable.setItems(drivers);

        driversTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            editButton.setDisable(newSelection == null);
            deleteButton.setDisable(newSelection == null);
        });
    }

    @FXML
    private void addDriver() {
        showAddEditDialog(null);
    }

    @FXML
    private void editDriver() {
        Driver selected = driversTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showAddEditDialog(selected);
        }
    }

    @FXML
    private void deleteDriver() {
        Driver selected = driversTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete this driver?");
            alert.setContentText("Name: " + selected.getFirstName() + " " + selected.getLastName());
            if (alert.showAndWait().get() == ButtonType.OK) {
                driverRepository.delete(selected.getId());
                drivers.remove(selected);
            }
        }
    }

    private void showAddEditDialog(Driver driver) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/fhzwickau/reisewelle/admin/add-edit-driver.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(driver == null ? "Add Driver" : "Edit Driver");

            AddEditDriverController controller = loader.getController();
            controller.setDriver(driver);

            stage.setOnHidden(event -> drivers.setAll(driverRepository.findAll()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}