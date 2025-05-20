package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.model.Driver;
import de.fhzwickau.reisewelle.dao.DriverDao;
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

public class AdminDriverController extends BaseTableController<Driver> {

    @FXML
    private TableView<Driver> driversTable;
    @FXML
    private TableColumn<Driver, String> firstNameColumn;
    @FXML
    private TableColumn<Driver, String> lastNameColumn;
    @FXML
    private TableColumn<Driver, String> licenseNumberColumn;
    @FXML
    private TableColumn<Driver, String> statusColumn;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    private final BaseDao<Driver> driverDao = new DriverDao();

    @FXML
    protected void initialize() throws SQLException {
        firstNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName()));
        lastNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));
        licenseNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLicenseNumber()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().getName()));
        super.initialize();
    }

    @Override
    protected BaseDao<Driver> getDao() {
        return driverDao;
    }

    @Override
    protected TableView<Driver> getTableView() {
        return driversTable;
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
    protected UUID getId(Driver driver) {
        return driver.getId();
    }

    @Override
    protected Stage showAddEditDialog(Driver driver) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/fhzwickau/reisewelle/admin/add-edit-driver.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(driver == null ? "Add Driver" : "Edit Driver");

        AddEditDriverController controller = loader.getController();
        controller.setDriver(driver);

        stage.setOnHidden(event -> refreshData());
        stage.show();

        return stage;
    }

    @Override
    protected String getDeleteConfirmationMessage(Driver item) {
        return "Driver License Number: " + licenseNumberColumn;
    }
}