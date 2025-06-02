package de.fhzwickau.reisewelle.controller.admin.role_permission;

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
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class AddEditRolePermissionController {

    @FXML
    private ComboBox<UserRole> roleComboBox;

    @FXML
    private ComboBox<Permission> permissionComboBox;

    private final BaseDao<UserRolePermission> userRolePermissionBaseDao = new UserRolePermissionDao();
    private final BaseDao<UserRole> userRoleBaseDao = new UserRoleDao();
    private final BaseDao<Permission> permissionDao = new PermissionDao();

    private UserRolePermission userRolePermission;
    private Runnable onSaved;

    public void setPermission(UserRolePermission userRolePermission) {
        this.userRolePermission = userRolePermission;

        if (userRolePermission != null) {
            roleComboBox.setValue(userRolePermission.getUserRole());
            permissionComboBox.setValue(userRolePermission.getPermission());
        }
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

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

    @FXML
    private void save() {
        UserRole selectedRole = roleComboBox.getValue();
        Permission selectedPermission = permissionComboBox.getValue();

        if (selectedRole == null || selectedPermission == null) {
            AlertUtil.showError("Fehlende Eingabe", "Bitte wählen Sie sowohl eine Rolle als auch eine Berechtigung aus.");
            return;
        }

        try {
            if (userRolePermission == null) {
                userRolePermission = new UserRolePermission(UUID.randomUUID());
            }

            userRolePermission.setUserRole(selectedRole);
            userRolePermission.setPermission(selectedPermission);

            userRolePermissionBaseDao.save(userRolePermission);

            if (onSaved != null) {
                onSaved.run();
            }

            closeWindow();
        } catch (SQLException e) {
            AlertUtil.showError("Fehler beim Speichern", e.getMessage());
        }
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) roleComboBox.getScene().getWindow();
        stage.close();
    }
}
