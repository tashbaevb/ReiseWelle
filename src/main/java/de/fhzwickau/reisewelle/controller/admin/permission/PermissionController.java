package de.fhzwickau.reisewelle.controller.admin.permission;

import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.PermissionDao;
import de.fhzwickau.reisewelle.model.Permission;
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

public class PermissionController {

    @FXML
    private TableView<Permission> permissionsTable;

    @FXML
    private TableColumn<Permission, String> nameColumn;

    @FXML
    private Button editButton, deleteButton;

    private final BaseDao<Permission> permissionBaseDao = new PermissionDao();
    private final ObservableList<Permission> permissions = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPermissionName()));
        permissionsTable.setItems(permissions);

        permissionsTable.setOnMouseClicked(this::onRowSelected);

        loadRoles();
    }

    private void loadRoles() {
        permissions.clear();
        try {
            permissions.addAll(permissionBaseDao.findAll());
        } catch (SQLException sqle) {
            AlertUtil.showError("Fehler beim Laden der Berechtigung", sqle.getMessage());
        }
    }

    private void onRowSelected(MouseEvent event) {
        boolean hasSelection = permissionsTable.getSelectionModel().getSelectedItem() != null;
        editButton.setDisable(!hasSelection);
        deleteButton.setDisable(!hasSelection);
    }

    @FXML
    private void onAdd() {
        openAddEditDialog(null);
    }

    @FXML
    private void onEdit() {
        Permission selected = permissionsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openAddEditDialog(selected);
        }
    }

    @FXML
    private void onDelete() {
        Permission selected = permissionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        AlertUtil.showConfirmation("Möchten Sie die Berechtigung wirklich löschen?", confirmed -> {
            if (confirmed) {
                try {
                    permissionBaseDao.delete(selected.getId());
                    loadRoles();
                    AlertUtil.showInfo("Erfolg", "Die Berechtigung wurde gelöscht.");
                } catch (SQLException sqle) {
                    AlertUtil.showError("Fehler beim Löschen", sqle.getMessage());
                }
            }
        });
    }

    private void openAddEditDialog(Permission permission) {
        try {
            WindowUtil.showModalWindow(
                    "/de/fhzwickau/reisewelle/admin/permission/add-edit-permission.fxml",
                    permission == null ? "Berechtigung hinzufügen" : "Berechtigung bearbeiten",
                    (AddEditPermissionController controller) -> {
                        controller.setPermission(permission);
                        controller.setOnSaved(this::loadRoles);
                    },
                    this::loadRoles
            );
        } catch (IOException ioe) {
            AlertUtil.showError("Fehler beim Öffnen", ioe.getMessage());
        }
    }
}
