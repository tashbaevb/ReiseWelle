package de.fhzwickau.reisewelle.controller.admin.employee;

import de.fhzwickau.reisewelle.controller.admin.BaseTableController;
import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.EmployeeDao;
import de.fhzwickau.reisewelle.model.Employee;
import de.fhzwickau.reisewelle.utils.WindowUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class AdminEmployeeController extends BaseTableController<Employee> {

    @FXML
    private TableView<Employee> employeeTable;
    @FXML
    private TableColumn<Employee, String> vornameColumn, nachnameColumn, emailColumn, createdAt;
    @FXML
    private Button editButton, deleteButton;

    private final BaseDao<Employee> employeeDao = new EmployeeDao();

    @FXML
    protected void initialize() throws SQLException {
        vornameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVorname()));
        nachnameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNachname()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        createdAt.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getErstelltAm() != null ? cellData.getValue().getErstelltAm().toString() : ""));

        init(employeeDao, employeeTable, editButton, deleteButton);
    }

    @Override
    protected boolean isInUse(Employee employee) {
        // Логика проверки, используется ли сотрудник в связанных данных
        // Если нет зависимости — можно просто вернуть false
        // Если есть, то тут нужно обращаться к нужному DAO и проверять
        return false;
    }

    @Override
    protected String getInUseMessage() {
        return "Der Mitarbeiter wird derzeit verwendet und kann nicht gelöscht werden.";
    }

    @Override
    protected UUID getId(Employee employee) {
        return employee.getId();
    }

    @Override
    protected Stage showAddEditDialog(Employee employee) throws IOException {
        return WindowUtil.showModalWindow(
                "/de/fhzwickau/reisewelle/admin/employee/add-edit-employee.fxml",
                employee == null ? "Mitarbeiter hinzufügen" : "Mitarbeiter bearbeiten",
                controller -> ((AddEditEmployeeController) controller).setEmployee(employee),
                this::loadDataAsync
        );
    }

    @Override
    protected TableView<Employee> getTableView() {
        return employeeTable;
    }

    @Override
    protected String getDeleteConfirmationMessage(Employee employee) {
        return "Email: " + employee.getEmail();
    }
}
