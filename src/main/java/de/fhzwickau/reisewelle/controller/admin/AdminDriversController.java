package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.model.Driver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminDriversController {

    @FXML
    private TableView<Driver> driversTable;
    @FXML
    private TableColumn<Driver, String> firstNameColumn;
    @FXML
    private TableColumn<Driver, String> lastNameColumn;
    @FXML
    private TableColumn<Driver, String> licenseNumberColumn;
    @FXML
    private TableColumn<Driver, String> statusColumn;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    private ObservableList<Driver> drivers = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Настройка столбцов таблицы
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        licenseNumberColumn.setCellValueFactory(new PropertyValueFactory<>("licenseNumber"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statusId")); // Позже заменим на отображение имени статуса

        // Привязка данных к таблице
        driversTable.setItems(drivers);

        // Активация кнопок Edit/Delete при выборе строки
        driversTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            editButton.setDisable(newSelection == null);
            deleteButton.setDisable(newSelection == null);
        });
    }

    @FXML
    private void addDriver() {
        // TODO: Открыть форму для добавления водителя
    }

    @FXML
    private void editDriver() {
        // TODO: Открыть форму для редактирования выбранного водителя
    }

    @FXML
    private void deleteDriver() {
        // TODO: Удалить выбранного водителя
    }
}