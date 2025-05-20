package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.model.User;
import de.fhzwickau.reisewelle.model.UserRole;
import de.fhzwickau.reisewelle.dao.UserDao;
import de.fhzwickau.reisewelle.dao.UserRoleDao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class AdminCompanyRepController {

    @FXML private TableView<User> companyRepTable;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> createdAtColumn;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private ObservableList<User> companyReps = FXCollections.observableArrayList();
    private UserDao userDao = new UserDao();
    private UserRoleDao userRoleDao = new UserRoleDao();
    private UUID companyRepRoleId;

    @FXML
    private void initialize() throws SQLException {
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        roleColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            String roleName = user.getUserRole() != null ? user.getUserRole().getRoleName() : "Unknown";
            System.out.println("User: " + user.getEmail() + ", Role: " + roleName);
            return new SimpleStringProperty(roleName);
        });
        createdAtColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCreatedAt() != null ? cellData.getValue().getCreatedAt().toString() : ""));

        UserRole companyRepRole = userRoleDao.findAll().stream()
                .filter(role -> role.getRoleName().equals("Employee"))
                .findFirst()
                .orElse(null);
        if (companyRepRole == null) {
            System.out.println("Role 'Employee' not found in the database");
            return;
        }
        companyRepRoleId = companyRepRole.getId();
        System.out.println("Employee role ID: " + companyRepRoleId);

        // Загрузить только пользователей с ролью Employee
        companyReps.addAll(userDao.findAll().stream()
                .filter(user -> user.getUserRole() != null && user.getUserRole().getId().equals(companyRepRoleId))
                .toList());
        companyRepTable.setItems(companyReps);

        companyRepTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            editButton.setDisable(newSelection == null);
            deleteButton.setDisable(newSelection == null);
        });
    }

    @FXML
    private void addCompanyRep() {
        showAddEditDialog(null);
    }

    @FXML
    private void editCompanyRep() {
        User selected = companyRepTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showAddEditDialog(selected);
        }
    }

    @FXML
    private void deleteCompanyRep() throws SQLException {
        User selected = companyRepTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete this company representative?");
            alert.setContentText("Email: " + selected.getEmail());
            if (alert.showAndWait().get() == ButtonType.OK) {
                userDao.delete(selected.getId());
                companyReps.remove(selected);
            }
        }
    }

    private void showAddEditDialog(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/fhzwickau/reisewelle/admin/add-edit-user.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(user == null ? "Add Company Representative" : "Edit Company Representative");

            AddEditUserController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);
            controller.setRoleFilter(companyRepRoleId);

            stage.setOnHidden(event -> {
                System.out.println("Updating company reps table...");
                try {
                    companyReps.setAll(userDao.findAll().stream()
                            .filter(u -> u.getUserRole() != null && u.getUserRole().getId().equals(companyRepRoleId))
                            .toList());
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                    showErrorDialog("Database error", "Could not load updated bus list.");
                }
            });
            stage.show();
        } catch (IOException e) {
            System.err.println("Error opening dialog: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}