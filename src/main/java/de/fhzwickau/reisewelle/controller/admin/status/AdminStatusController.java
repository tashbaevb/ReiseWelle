package de.fhzwickau.reisewelle.controller.admin.status;

import de.fhzwickau.reisewelle.controller.admin.BaseTableController;
import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.BusDao;
import de.fhzwickau.reisewelle.dao.DriverDao;
import de.fhzwickau.reisewelle.dao.StatusDao;
import de.fhzwickau.reisewelle.model.Status;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class AdminStatusController extends BaseTableController<Status> {

    @FXML
    private TableView<Status> statusTable;

    @FXML
    private TableColumn<Status, String> nameColumn;

    @FXML
    private Button editButton, deleteButton;

    private final BaseDao<Status> statusDao = new StatusDao();
    private final BusDao busDao = new BusDao();
    private final DriverDao driverDao = new DriverDao();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        init(statusDao, statusTable, editButton, deleteButton);
    }

    @Override
    protected boolean isInUse(Status status) {
        try {
            return busDao.isBusWithStatusId(status.getId()) || driverDao.isDriverWithStatusId(status.getId());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return true;
        }
    }

    @Override
    protected String getInUseMessage() {
        return "Status ist in Verwendung und kann nicht gelöscht werden.";
    }

    @Override
    protected UUID getId(Status status) {
        return status.getId();
    }

    @Override
    protected Stage showAddEditDialog(Status status) throws IOException {
        return AddEditStatusController.showDialog(status, v -> loadDataAsync());
    }

    @Override
    protected String getDeleteConfirmationMessage(Status status) {
        return "Status: " + status.getName();
    }

    @Override
    protected TableView<Status> getTableView() {
        return statusTable;
    }
}
