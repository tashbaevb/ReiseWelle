package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.model.Bus;
import de.fhzwickau.reisewelle.dao.BusDao;
import javafx.beans.property.SimpleIntegerProperty;
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

public class AdminBusController extends BaseTableController<Bus> {

    @FXML private TableView<Bus> busesTable;
    @FXML private TableColumn<Bus, String> busNumberColumn;
    @FXML private TableColumn<Bus, Integer> totalSeatsColumn;
    @FXML private TableColumn<Bus, String> statusColumn;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private final BaseDao<Bus> busDao = new BusDao();

    @FXML
    protected void initialize() throws SQLException {
        busNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBusNumber()));
        totalSeatsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTotalSeats()).asObject());
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus() != null ? cellData.getValue().getStatus().getName() : ""));
        super.initialize();
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/fhzwickau/reisewelle/admin/add-edit-bus.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(bus == null ? "Add Bus" : "Edit Bus");

        AddEditBusController controller = loader.getController();
        controller.setBus(bus);

        stage.setOnHidden(event -> refreshData());
        stage.show();

        return stage;
    }

    @Override
    protected String getDeleteConfirmationMessage(Bus bus) {
        return "Bus Number: " + bus.getBusNumber();
    }
}