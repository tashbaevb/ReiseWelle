package de.fhzwickau.reisewelle.controller.admin.user_role;

import de.fhzwickau.reisewelle.controller.admin.BaseTableController;
import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.UserRoleDao;
import de.fhzwickau.reisewelle.model.UserRole;
import de.fhzwickau.reisewelle.utils.WindowUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.UUID;

public class UserRoleController extends BaseTableController<UserRole> {

    @FXML
    private TableView<UserRole> userRolesTable;

    @FXML
    private TableColumn<UserRole, String> nameColumn;

    @FXML
    private Button editButton, deleteButton;

    private final BaseDao<UserRole> userRoleDao = new UserRoleDao();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRoleName()));
        init(userRoleDao, userRolesTable, editButton, deleteButton);
    }

    @Override
    protected boolean isInUse(UserRole role) {
        return false;
    }

    @Override
    protected String getInUseMessage() {
        return "Diese Rolle wird verwendet und kann nicht gelöscht werden.";
    }

    @Override
    protected UUID getId(UserRole role) {
        return role.getId();
    }

    @Override
    protected Stage showAddEditDialog(UserRole role) throws IOException {
        return WindowUtil.showModalWindow(
                "/de/fhzwickau/reisewelle/admin/user_role/add-edit-userRole.fxml",
                role == null ? "Rolle hinzufügen" : "Rolle bearbeiten",
                controller -> {
                    AddEditUserRoleController c = (AddEditUserRoleController) controller;
                    c.setRole(role);
                    c.setOnSaved(this::loadDataAsync);
                },
                this::loadDataAsync
        );
    }

    @Override
    protected String getDeleteConfirmationMessage(UserRole role) {
        return "Rolle: " + role.getRoleName();
    }

    @Override
    protected TableView<UserRole> getTableView() {
        return userRolesTable;
    }
}
