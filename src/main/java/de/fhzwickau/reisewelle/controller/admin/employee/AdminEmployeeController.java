package de.fhzwickau.reisewelle.controller.admin.employee;

import de.fhzwickau.reisewelle.controller.admin.BaseTableController;
import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.EmployeeDao;
import de.fhzwickau.reisewelle.model.Employee;
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
import java.util.List;
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
        createdAt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getErstelltAm() != null ? cellData.getValue().getErstelltAm().toString() : ""));
        List<Employee> employees = ((EmployeeDao) employeeDao).findAll();
        items.setAll(employees);
        employeeTable.setItems(items);

        employeeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            editButton.setDisable(newSelection == null);
            deleteButton.setDisable(newSelection == null);
        });
    }

    @Override
    protected BaseDao<Employee> getDao() {
        return employeeDao;
    }

    @Override
    protected TableView<Employee> getTableView() {
        return employeeTable;
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
    protected UUID getId(Employee employee) {
        return employee.getId();
    }

    @Override
    protected Stage showAddEditDialog(Employee user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/fhzwickau/reisewelle/admin/employee/add-edit-empoyee.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(user == null ? "Mitarbeiter hinzufügen" : "Mitarbeiter bearbeiten");

        AddEditEmployeeController controller = loader.getController();
        controller.setEmployee(user);

        stage.setOnHidden(event -> refreshData());
        stage.show();
        return stage;
    }

    @Override
    protected String getDeleteConfirmationMessage(Employee employee) {
        return "Email: " + employee.getEmail();
    }
}