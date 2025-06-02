package de.fhzwickau.reisewelle.controller.admin.user;

import de.fhzwickau.reisewelle.config.AccessManager;
import de.fhzwickau.reisewelle.controller.admin.BaseTableController;
import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.UserDao;
import de.fhzwickau.reisewelle.model.User;
import de.fhzwickau.reisewelle.utils.PermissionsUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.UUID;

public class AdminUserController extends BaseTableController<User> {

    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, String> emailColumn, createdAtColumn;
    @FXML
    private Button deleteButton;

    private final BaseDao<User> userDao = new UserDao();

    @FXML
    protected void initialize() {
        emailColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        createdAtColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCreatedAt() != null
                ? c.getValue().getCreatedAt().toString() : "")
        );

        init(userDao, usersTable, deleteButton, deleteButton);
    }

    @Override
    protected boolean isInUse(User user) {
        return !AccessManager.hasPermission(PermissionsUtil.KUNDEN_LOESCHEN);
    }

    @Override
    protected String getInUseMessage() {
        return "Sie haben nicht die Berechtigung, diesen Benutzer zu löschen.";
    }

    @Override
    protected UUID getId(User user) {
        return user.getId();
    }

    @Override
    protected String getDeleteConfirmationMessage(User user) {
        return "Möchten Sie den Kunden: " + user.getEmail() + " wirklich löschen?";
    }

    @Override
    protected Stage showAddEditDialog(User user) {
        throw new UnsupportedOperationException("Sie können den Kunden nicht löschen");
    }

    @Override
    protected TableView<User> getTableView() {
        return usersTable;
    }
}
