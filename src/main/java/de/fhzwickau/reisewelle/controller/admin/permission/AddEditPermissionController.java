package de.fhzwickau.reisewelle.controller.admin.permission;

import de.fhzwickau.reisewelle.controller.admin.BaseAddEditController;
import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.PermissionDao;
import de.fhzwickau.reisewelle.model.Permission;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.UUID;

public class AddEditPermissionController extends BaseAddEditController<Permission> {

    @FXML
    private TextField nameField;

    private final BaseDao<Permission> permissionBaseDao = new PermissionDao();
    private Runnable onSaved;

    public void setPermission(Permission permission) {
        this.entity = permission;
        if (entity != null) {
            nameField.setText(entity.getPermissionName());
        }
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    @Override
    protected void saveEntity() throws SQLException {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            throw new SQLException("Der Rollenname darf nicht leer sein.");
        }

        if (entity == null) {
            entity = new Permission(UUID.randomUUID(), name);
        } else {
            entity.setPermissionName(name);
        }

        permissionBaseDao.save(entity);

        if (onSaved != null) onSaved.run();
    }

    @Override
    protected javafx.scene.Node getAnyControl() {
        return nameField;
    }

    @FXML
    protected void save() {
        try {
            saveEntity();
            close();
        } catch (SQLException e) {
            AlertUtil.showError("Fehler beim Speichern", e.getMessage());
        }
    }

    @FXML
    protected void cancel() {
        close();
    }
}
