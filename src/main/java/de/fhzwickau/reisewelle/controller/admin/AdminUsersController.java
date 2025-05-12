package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.model.User;
import de.fhzwickau.reisewelle.repository.UserRepository;
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
import java.util.List;

public class AdminUsersController {

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> createdAtColumn;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private ObservableList<User> users = FXCollections.observableArrayList();
    private UserRepository userRepository = new UserRepository();

    @FXML
    private void initialize() {
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        roleColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            String roleName = user.getUserRole() != null ? user.getUserRole().getRoleName() : "N/A";
            return new SimpleStringProperty(roleName);
        });
        createdAtColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCreatedAt() != null ? cellData.getValue().getCreatedAt().toString() : ""));

        // Загрузка пользователей
        List<User> loadedUsers = userRepository.findAll();
        System.out.println("Loaded users count: " + loadedUsers.size());
        for (User user : loadedUsers) {
            System.out.println("User loaded: email=" + user.getEmail() + ", role=" + (user.getUserRole() != null ? user.getUserRole().getRoleName() : "null"));
        }
        users.addAll(loadedUsers);
        usersTable.setItems(users);

        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            editButton.setDisable(newSelection == null);
            deleteButton.setDisable(newSelection == null);
        });
    }

    @FXML
    private void addUser() {
        showAddEditDialog(null);
    }

    @FXML
    private void editUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showAddEditDialog(selected);
        }
    }

    @FXML
    private void deleteUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete this user?");
            alert.setContentText("Email: " + selected.getEmail());
            if (alert.showAndWait().get() == ButtonType.OK) {
                userRepository.delete(selected.getId());
                users.remove(selected);
            }
        }
    }

    private void showAddEditDialog(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/fhzwickau/reisewelle/admin/add-edit-user.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(user == null ? "Add User" : "Edit User");

            AddEditUserController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage); // Передаем Stage

            stage.setOnHidden(event -> {
                System.out.println("Updating users table...");
                users.setAll(userRepository.findAll());
            });
            stage.show();
        } catch (IOException e) {
            System.err.println("Error opening dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
}