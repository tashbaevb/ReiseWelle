package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminUsersController {

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> roleColumn;


    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    private ObservableList<User> users = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("roleId"));

        userTable.setItems(users);

        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            editButton.setDisable(newSelection == null);
            deleteButton.setDisable(newSelection == null);
        });
    }

    @FXML
    private void addUser() {
        // TODO: Открыть форму для добавления пользователя
    }

    @FXML
    private void editUser() {
        // TODO: Открыть форму для редактирования выбранного пользователя
    }

    @FXML
    private void deleteUser() {
        // TODO: Удалить выбранного пользователя
    }

}
