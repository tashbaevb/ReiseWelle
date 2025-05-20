package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.model.User;
import de.fhzwickau.reisewelle.model.UserRole;
import de.fhzwickau.reisewelle.dao.UserDao;
import de.fhzwickau.reisewelle.dao.UserRoleDao;
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

public class AdminEmployeeController extends BaseTableController<User> {

    @FXML
    private TableView<User> employeeTable;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> createdAtColumn;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    private final BaseDao<User> userDao = new UserDao();
    private final BaseDao<UserRole> userRoleDao = new UserRoleDao();

    @FXML
    protected void initialize() throws SQLException {
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        createdAtColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCreatedAt() != null ? cellData.getValue().getCreatedAt().toString() : ""));
        List<User> employees = ((UserDao) userDao).findEmployees();
        items.setAll(employees);
        employeeTable.setItems(items);

        employeeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            editButton.setDisable(newSelection == null);
            deleteButton.setDisable(newSelection == null);
        });
    }

    @Override
    protected BaseDao<User> getDao() {
        return userDao;
    }

    @Override
    protected TableView<User> getTableView() {
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
    protected UUID getId(User user) {
        return user.getId();
    }

    @Override
    protected Stage showAddEditDialog(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/fhzwickau/reisewelle/admin/add-edit-user.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(user == null ? "Add Company Representative" : "Edit Company Representative");

        AddEditUserController controller = loader.getController();
        controller.setUser(user);

        UserRoleDao roleDao = new UserRoleDao();
        try {
            assert user != null;
            UserRole employeeRole = roleDao.findByName("Employee");
            controller.setFixedUserRole(employeeRole);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        stage.setOnHidden(event -> refreshData());
        stage.show();
        return stage;
    }

    @Override
    protected String getDeleteConfirmationMessage(User user) {
        return "Email: " + user.getEmail();
    }
}