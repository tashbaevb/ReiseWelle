package de.fhzwickau.reisewelle.controller.admin.user_role;

import de.fhzwickau.reisewelle.controller.admin.BaseAddEditController;
import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.UserRoleDao;
import de.fhzwickau.reisewelle.model.UserRole;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.Node;

import java.sql.SQLException;
import java.util.UUID;

public class AddEditUserRoleController extends BaseAddEditController<UserRole> {


    @FXML
    private TextField nameField;

    private final BaseDao<UserRole> userRoleDao = new UserRoleDao();
    private Runnable onSaved;

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    public void setRole(UserRole role) {
        this.entity = role;
        if (entity != null) {
            nameField.setText(entity.getRoleName());
        }
    }

    @Override
    protected void saveEntity() throws SQLException {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            AlertUtil.showError("Fehler", "Der Rollenname darf nicht leer sein.");
        }
        if (entity == null) {
            entity = new UserRole(UUID.randomUUID(), name);
        } else {
            entity.setRoleName(name);
        }
        userRoleDao.save(entity);

        if (onSaved != null) {
            onSaved.run();
        }
    }

    @Override
    protected Node getAnyControl() {
        return nameField;
    }
}
