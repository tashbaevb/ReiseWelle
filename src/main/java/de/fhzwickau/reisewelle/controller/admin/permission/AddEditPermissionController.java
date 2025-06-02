package de.fhzwickau.reisewelle.controller.admin.permission;

import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.PermissionDao;
import de.fhzwickau.reisewelle.model.Permission;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.UUID;

public class AddEditPermissionController {

    @FXML
    private TextField nameField;

    private final BaseDao<Permission> permissionBaseDao = new PermissionDao();
    private Permission permission;
    private Runnable onSaved;

    public void setPermission(Permission permission) {
        this.permission = permission;
        if (permission != null) {
            nameField.setText(permission.getPermissionName());
        }
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    @FXML
    private void save() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            AlertUtil.showError("Ungültiger Name", "Der Rollenname darf nicht leer sein.");
            return;
        }

        try {
            if (permission == null) {
                permission = new Permission(UUID.randomUUID(), name);
            } else {
                permission.setPermissionName(name);
            }
            permissionBaseDao.save(permission);

            if (onSaved != null) onSaved.run();
            closeWindow();
        } catch (SQLException sqle) {
            AlertUtil.showError("Fehler beim Speichern", sqle.getMessage());
        }
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
