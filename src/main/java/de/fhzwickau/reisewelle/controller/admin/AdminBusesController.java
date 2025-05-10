package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.model.Bus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminBusesController {

    @FXML
    private TableView<Bus> busesTable;
    @FXML
    private TableColumn<Bus, String> nameColumn;
    @FXML
    private TableColumn<Bus, Integer> totalSeatsColumn;
    @FXML
    private TableColumn<Bus, String> statusColumn;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    private ObservableList<Bus> buses = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Настройка столбцов таблицы
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        totalSeatsColumn.setCellValueFactory(new PropertyValueFactory<>("totalSeats"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statusId")); // Позже заменим на отображение имени статуса

        // Привязка данных к таблице
        busesTable.setItems(buses);

        // Активация кнопок Edit/Delete при выборе строки
        busesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            editButton.setDisable(newSelection == null);
            deleteButton.setDisable(newSelection == null);
        });
    }

    @FXML
    private void addBus() {
        // TODO: Открыть форму для добавления автобуса
    }

    @FXML
    private void editBus() {
        // TODO: Открыть форму для редактирования выбранного автобуса
    }

    @FXML
    private void deleteBus() {
        // TODO: Удалить выбранный автобус
    }
}