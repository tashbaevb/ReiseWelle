package de.fhzwickau.reisewelle.controller.admin.role_permission;

import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.UserRolePermissionDao;
import de.fhzwickau.reisewelle.model.UserRolePermission;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import de.fhzwickau.reisewelle.utils.WindowUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.sql.SQLException;

public class RolePermissionController {

    @FXML
    private TableView<UserRolePermission> rolePermissionsTable;

    @FXML
    private TableColumn<UserRolePermission, String> roleNameColumn, permissionNameColumn;

    @FXML
    private Button editButton, deleteButton;

    private final BaseDao<UserRolePermission> userRolePermissionBaseDao = new UserRolePermissionDao();
    private final ObservableList<UserRolePermission> userRolePermissions = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        roleNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUserRole().getRoleName()));
        permissionNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPermission().getPermissionName()));
        rolePermissionsTable.setItems(userRolePermissions);

        rolePermissionsTable.setOnMouseClicked(this::onRowSelected);

        loadRoles();
    }

    private void loadRoles() {
        userRolePermissions.clear();
        try {
            userRolePermissions.addAll(userRolePermissionBaseDao.findAll());
        } catch (SQLException sqle) {
            AlertUtil.showError("Fehler beim Laden der Role-Berechtigung", sqle.getMessage());
        }
    }

    private void onRowSelected(MouseEvent event) {
        boolean hasSelection = rolePermissionsTable.getSelectionModel().getSelectedItem() != null;
        editButton.setDisable(!hasSelection);
        deleteButton.setDisable(!hasSelection);
    }

    @FXML
    private void onAdd() {
        openAddEditDialog(null);
    }

    @FXML
    private void onEdit() {
        UserRolePermission selected = rolePermissionsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openAddEditDialog(selected);
        }
    }

    @FXML
    private void onDelete() {
        UserRolePermission selected = rolePermissionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        AlertUtil.showConfirmation("Möchten Sie die Role-Berechtigung wirklich löschen?", confirmed -> {
            if (confirmed) {
                try {
                    userRolePermissionBaseDao.delete(selected.getId());
                    loadRoles();
                    AlertUtil.showInfo("Erfolg", "Die Role-Berechtigung wurde gelöscht.");
                } catch (SQLException sqle) {
                    AlertUtil.showError("Fehler beim Löschen", sqle.getMessage());
                }
            }
        });
    }

    private void openAddEditDialog(UserRolePermission userRolePermission) {
        try {
            WindowUtil.showModalWindow(
                    "/de/fhzwickau/reisewelle/admin/role-permission/add-edit-role-permission.fxml",
                    userRolePermission == null ? "Role-Berechtigung hinzufügen" : "Role-Berechtigung bearbeiten",
                    (AddEditRolePermissionController controller) -> {
                        controller.setPermission(userRolePermission);
                        controller.setOnSaved(this::loadRoles);
                    },
                    this::loadRoles
            );
        } catch (IOException ioe) {
            AlertUtil.showError("Fehler beim Öffnen", ioe.getMessage());
        }
    }
}
