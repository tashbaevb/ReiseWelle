package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.model.User;
import de.fhzwickau.reisewelle.dao.UserDao;
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

public class AdminUserController extends BaseTableController<User> {

    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> roleColumn;
    @FXML
    private TableColumn<User, String> createdAtColumn;
    @FXML
    private Button deleteButton;

    private final BaseDao<User> userDao = new UserDao();

    @FXML
    protected void initialize() throws SQLException {
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        roleColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            String roleName = user.getUserRole() != null ? user.getUserRole().getRoleName() : "N/A";
            return new SimpleStringProperty(roleName);
        });
        createdAtColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCreatedAt() != null ? cellData.getValue().getCreatedAt().toString() : ""));
        super.initialize();
    }

    @Override
    protected BaseDao<User> getDao() {
        return userDao;
    }

    @Override
    protected TableView<User> getTableView() {
        return usersTable;
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
        stage.setTitle(user == null ? "Add User" : "Edit User");

        AddEditUserController controller = loader.getController();
        controller.setUser(user);

        stage.setOnHidden(event -> refreshData());
        stage.show();

        return stage;
    }

    @Override
    protected String getDeleteConfirmationMessage(User user) {
        return "Delete user: " + user.getEmail() + "?";
    }

    // Not Realized
    @Override
    protected Button getEditButton() {
        return null;
    }
}