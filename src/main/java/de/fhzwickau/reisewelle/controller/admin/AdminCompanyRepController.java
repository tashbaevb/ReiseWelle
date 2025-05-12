package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.model.User;
import de.fhzwickau.reisewelle.model.UserRole;
import de.fhzwickau.reisewelle.repository.UserRepository;
import de.fhzwickau.reisewelle.repository.UserRoleRepository;
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
    private UserRepository userRepository = new UserRepository();
    private UserRoleRepository userRoleRepository = new UserRoleRepository();
    private UUID companyRepRoleId;

    @FXML
    private void initialize() {
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        roleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserRole().getRoleName()));
        createdAtColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCreatedAt() != null ? cellData.getValue().getCreatedAt().toString() : ""));

        // Найти роль CompanyRep
        UserRole companyRepRole = userRoleRepository.findAll().stream()
                .filter(role -> role.getRoleName().equals("CompanyRep"))
                .findFirst()
                .orElse(null);
        if (companyRepRole == null) {
            System.out.println("Role 'CompanyRep' not found in the database");
            return;
        }
        companyRepRoleId = companyRepRole.getId();
        System.out.println("CompanyRep role ID: " + companyRepRoleId);

        // Загрузить только пользователей с ролью CompanyRep
        companyReps.addAll(userRepository.findAll().stream()
                .filter(user -> user.getUserRole().getId().equals(companyRepRoleId))
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
    private void deleteCompanyRep() {
        User selected = companyRepTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete this company representative?");
            alert.setContentText("Email: " + selected.getEmail());
            if (alert.showAndWait().get() == ButtonType.OK) {
                userRepository.delete(selected.getId());
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
            controller.setRoleFilter(companyRepRoleId); // Ограничим выбор роли только CompanyRep

            stage.setOnHidden(event -> {
                System.out.println("Updating company reps table...");
                companyReps.setAll(userRepository.findAll().stream()
                        .filter(u -> u.getUserRole().getId().equals(companyRepRoleId))
                        .toList());
            });
            stage.show();
        } catch (IOException e) {
            System.err.println("Error opening dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
}