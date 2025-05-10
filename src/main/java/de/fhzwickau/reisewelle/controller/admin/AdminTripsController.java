package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.model.Trip;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminTripsController {

    @FXML
    private TableView<Trip> tripsTable;
    @FXML
    private TableColumn<Trip, String> idColumn;
    @FXML
    private TableColumn<Trip, String> busIdColumn;
    @FXML
    private TableColumn<Trip, String> driverIdColumn;
    @FXML
    private TableColumn<Trip, String> departureDateColumn;
    @FXML
    private TableColumn<Trip, String> statusColumn;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    private ObservableList<Trip> trips = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Настройка столбцов таблицы
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        busIdColumn.setCellValueFactory(new PropertyValueFactory<>("busId"));
        driverIdColumn.setCellValueFactory(new PropertyValueFactory<>("driverId"));
        departureDateColumn.setCellValueFactory(new PropertyValueFactory<>("departureDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statusId")); // Позже заменим на отображение имени статуса

        // Привязка данных к таблице
        tripsTable.setItems(trips);

        // Активация кнопок Edit/Delete при выборе строки
        tripsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            editButton.setDisable(newSelection == null);
            deleteButton.setDisable(newSelection == null);
        });
    }

    @FXML
    private void addTrip() {
        // TODO: Открыть форму для добавления поездки
    }

    @FXML
    private void editTrip() {
        // TODO: Открыть форму для редактирования выбранной поездки
    }

    @FXML
    private void deleteTrip() {
        // TODO: Удалить выбранную поездку
    }
}