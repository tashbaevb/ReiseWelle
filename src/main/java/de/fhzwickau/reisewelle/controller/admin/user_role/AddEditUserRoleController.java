package de.fhzwickau.reisewelle.controller.admin.user_role;

import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.UserRoleDao;
import de.fhzwickau.reisewelle.model.UserRole;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.UUID;

public class AddEditUserRoleController {

    @FXML
    private TextField nameField;

    private final BaseDao<UserRole> userRoleDao = new UserRoleDao();
    private UserRole role;
    private Runnable onSaved;

    public void setRole(UserRole role) {
        this.role = role;
        if (role != null) {
            nameField.setText(role.getRoleName());
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
            if (role == null) {
                role = new UserRole(UUID.randomUUID(), name);
            } else {
                role.setRoleName(name);
            }
            userRoleDao.save(role);

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
