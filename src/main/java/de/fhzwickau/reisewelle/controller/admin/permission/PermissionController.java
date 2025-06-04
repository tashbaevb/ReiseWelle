package de.fhzwickau.reisewelle.controller.admin.permission;

import de.fhzwickau.reisewelle.controller.admin.BaseTableController;
import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.PermissionDao;
import de.fhzwickau.reisewelle.model.Permission;
import de.fhzwickau.reisewelle.utils.WindowUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.UUID;

public class PermissionController extends BaseTableController<Permission> {

    @FXML
    private TableView<Permission> permissionsTable;

    @FXML
    private TableColumn<Permission, String> nameColumn;

    @FXML
    private Button editButton, deleteButton;

    private final BaseDao<Permission> permissionDao = new PermissionDao();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPermissionName()));
        init(permissionDao, permissionsTable, editButton, deleteButton);
    }

    @Override
    protected boolean isInUse(Permission permission) {
        return false;
    }

    @Override
    protected String getInUseMessage() {
        return "Diese Berechtigung wird verwendet und kann nicht gelöscht werden.";
    }

    @Override
    protected UUID getId(Permission permission) {
        return permission.getId();
    }

    @Override
    protected Stage showAddEditDialog(Permission permission) throws IOException {
        return WindowUtil.showModalWindow(
                "/de/fhzwickau/reisewelle/admin/permission/add-edit-permission.fxml",
                permission == null ? "Berechtigung hinzufügen" : "Berechtigung bearbeiten",
                controller -> {
                    AddEditPermissionController c = (AddEditPermissionController) controller;
                    c.setPermission(permission);
                    c.setOnSaved(this::loadDataAsync);
                },
                this::loadDataAsync
        );
    }

    @Override
    protected String getDeleteConfirmationMessage(Permission permission) {
        return "Berechtigung: " + permission.getPermissionName();
    }

    @Override
    protected TableView<Permission> getTableView() {
        return permissionsTable;
    }
}
