package de.fhzwickau.reisewelle.controller.admin.role_permission;

import de.fhzwickau.reisewelle.controller.admin.BaseAddEditController;
import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.PermissionDao;
import de.fhzwickau.reisewelle.dao.UserRoleDao;
import de.fhzwickau.reisewelle.dao.UserRolePermissionDao;
import de.fhzwickau.reisewelle.model.Permission;
import de.fhzwickau.reisewelle.model.UserRole;
import de.fhzwickau.reisewelle.model.UserRolePermission;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class AddEditRolePermissionController extends BaseAddEditController<UserRolePermission> {

    @FXML
    private ComboBox<UserRole> roleComboBox;

    @FXML
    private ComboBox<Permission> permissionComboBox;

    private final BaseDao<UserRolePermission> userRolePermissionBaseDao = new UserRolePermissionDao();
    private final BaseDao<UserRole> userRoleBaseDao = new UserRoleDao();
    private final BaseDao<Permission> permissionDao = new PermissionDao();

    private Runnable onSaved;

    @FXML
    private void initialize() {
        try {
            List<UserRole> roles = userRoleBaseDao.findAll();
            List<Permission> permissions = permissionDao.findAll();

            roleComboBox.setItems(FXCollections.observableArrayList(roles));
            permissionComboBox.setItems(FXCollections.observableArrayList(permissions));
        } catch (SQLException sqle) {
            AlertUtil.showError("Fehler beim Laden der Rollen/Berechtigungen", sqle.getMessage());
        }
    }

    public void setUserRolePermission(UserRolePermission userRolePermission) {
        this.entity = userRolePermission;

        if (entity != null) {
            roleComboBox.setValue(entity.getUserRole());
            permissionComboBox.setValue(entity.getPermission());
        } else {
            roleComboBox.getSelectionModel().clearSelection();
            permissionComboBox.getSelectionModel().clearSelection();
        }
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    @Override
    protected void saveEntity() throws SQLException {
        UserRole selectedRole = roleComboBox.getValue();
        Permission selectedPermission = permissionComboBox.getValue();

        if (selectedRole == null || selectedPermission == null) {
            throw new SQLException("Bitte wählen Sie sowohl eine Rolle als auch eine Berechtigung aus.");
        }

        if (entity == null) {
            entity = new UserRolePermission(UUID.randomUUID());
        }

        entity.setUserRole(selectedRole);
        entity.setPermission(selectedPermission);

        userRolePermissionBaseDao.save(entity);

        if (onSaved != null) {
            onSaved.run();
        }
    }

    @Override
    protected Node getAnyControl() {
        return roleComboBox;
    }

    @FXML
    protected void save() {
        try {
            saveEntity();
            close();
        } catch (SQLException e) {
            AlertUtil.showError("Fehler beim Speichern", e.getMessage());
            if (roleComboBox.getValue() == null) {
                roleComboBox.setStyle("-fx-border-color: red;");
            } else {
                roleComboBox.setStyle(null);
            }
            if (permissionComboBox.getValue() == null) {
                permissionComboBox.setStyle("-fx-border-color: red;");
            } else {
                permissionComboBox.setStyle(null);
            }
        }
    }

    @FXML
    protected void cancel() {
        close();
    }
}
