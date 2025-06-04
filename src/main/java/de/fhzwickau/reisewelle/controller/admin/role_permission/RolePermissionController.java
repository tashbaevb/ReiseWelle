package de.fhzwickau.reisewelle.controller.admin.role_permission;

import de.fhzwickau.reisewelle.controller.admin.BaseTableController;
import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.UserRolePermissionDao;
import de.fhzwickau.reisewelle.model.UserRolePermission;
import de.fhzwickau.reisewelle.utils.WindowUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.UUID;

public class RolePermissionController extends BaseTableController<UserRolePermission> {

    @FXML
    private TableView<UserRolePermission> rolePermissionsTable;

    @FXML
    private TableColumn<UserRolePermission, String> roleNameColumn, permissionNameColumn;

    @FXML
    private Button editButton, deleteButton;

    private final BaseDao<UserRolePermission> userRolePermissionDao = new UserRolePermissionDao();

    @FXML
    public void initialize() {
        roleNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUserRole().getRoleName()));
        permissionNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPermission().getPermissionName()));

        init(userRolePermissionDao, rolePermissionsTable, editButton, deleteButton);
    }

    @Override
    protected boolean isInUse(UserRolePermission entity) {
        return false;
    }

    @Override
    protected String getInUseMessage() {
        return "Diese Rolle-Berechtigung kann nicht gelöscht werden, da sie verwendet wird.";
    }

    @Override
    protected UUID getId(UserRolePermission entity) {
        return entity.getId();
    }

    @Override
    protected Stage showAddEditDialog(UserRolePermission entity) throws IOException {
        return WindowUtil.showModalWindow(
                "/de/fhzwickau/reisewelle/admin/role-permission/add-edit-role-permission.fxml",
                entity == null ? "Role-Berechtigung hinzufügen" : "Role-Berechtigung bearbeiten",
                controller -> {
                    AddEditRolePermissionController c = (AddEditRolePermissionController) controller;
                    c.setUserRolePermission(entity);
                    c.setOnSaved(this::loadDataAsync);
                },
                this::loadDataAsync
        );
    }

    @Override
    protected String getDeleteConfirmationMessage(UserRolePermission entity) {
        return "Rolle: " + entity.getUserRole().getRoleName() + ", Berechtigung: " + entity.getPermission().getPermissionName();
    }

    @Override
    protected TableView<UserRolePermission> getTableView() {
        return rolePermissionsTable;
    }
}
