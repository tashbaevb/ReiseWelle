package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminCompanyRepsController {

    @FXML
    private TableView<User> companyRepsTable;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> createdAtColumn;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    private ObservableList<User> companyReps = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Настройка столбцов таблицы
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        // Привязка данных к таблице
        companyRepsTable.setItems(companyReps);

        // Активация кнопок Edit/Delete при выборе строки
        companyRepsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            editButton.setDisable(newSelection == null);
            deleteButton.setDisable(newSelection == null);
        });
    }

    @FXML
    private void addCompanyRep() {
        // TODO: Открыть форму для добавления представителя компании
    }

    @FXML
    private void editCompanyRep() {
        // TODO: Открыть форму для редактирования выбранного представителя
    }

    @FXML
    private void deleteCompanyRep() {
        // TODO: Удалить выбранного представителя
    }
}