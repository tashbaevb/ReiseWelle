package de.fhzwickau.reisewelle.controller.admin.user_role;

import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.UserRoleDao;
import de.fhzwickau.reisewelle.model.UserRole;
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

public class UserRoleController {

    @FXML
    private TableView<UserRole> userRolesTable;

    @FXML
    private TableColumn<UserRole, String> nameColumn;

    @FXML
    private Button editButton, deleteButton;

    private final BaseDao<UserRole> userRoleDao = new UserRoleDao();
    private final ObservableList<UserRole> userRoles = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRoleName()));
        userRolesTable.setItems(userRoles);

        userRolesTable.setOnMouseClicked(this::onRowSelected);

        loadRoles();
    }

    private void loadRoles() {
        userRoles.clear();
        try {
            userRoles.addAll(userRoleDao.findAll());
        } catch (SQLException sqle) {
            AlertUtil.showError("Fehler beim Laden der Rollen", sqle.getMessage());
        }
    }

    private void onRowSelected(MouseEvent event) {
        boolean hasSelection = userRolesTable.getSelectionModel().getSelectedItem() != null;
        editButton.setDisable(!hasSelection);
        deleteButton.setDisable(!hasSelection);
    }

    @FXML
    private void onAdd() {
        openAddEditDialog(null);
    }

    @FXML
    private void onEdit() {
        UserRole selected = userRolesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openAddEditDialog(selected);
        }
    }

    @FXML
    private void onDelete() {
        UserRole selected = userRolesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        AlertUtil.showConfirmation("Möchten Sie die Rolle wirklich löschen?", confirmed -> {
            if (confirmed) {
                try {
                    userRoleDao.delete(selected.getId());
                    loadRoles();
                    AlertUtil.showInfo("Erfolg", "Die Rolle wurde gelöscht.");
                } catch (SQLException sqle) {
                    AlertUtil.showError("Fehler beim Löschen", sqle.getMessage());
                }
            }
        });
    }

    private void openAddEditDialog(UserRole role) {
        try {
            WindowUtil.showModalWindow(
                    "/de/fhzwickau/reisewelle/admin/user_role/add-edit-userRole.fxml",
                    role == null ? "Rolle hinzufügen" : "Rolle bearbeiten",
                    (AddEditUserRoleController controller) -> {
                        controller.setRole(role);
                        controller.setOnSaved(this::loadRoles);
                    },
                    this::loadRoles
            );
        } catch (IOException ioe) {
            AlertUtil.showError("Fehler beim Öffnen", ioe.getMessage());
        }
    }
}
