package de.fhzwickau.reisewelle.controller.admin.city;

import de.fhzwickau.reisewelle.dao.CityDao;
import de.fhzwickau.reisewelle.model.City;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdminCityController {

    @FXML
    private TableView<City> citiesTable;
    @FXML
    private TableColumn<City, String> nameColumn;
    @FXML
    private TableColumn<City, String> countryColumn;
    @FXML
    private Button addButton, editButton, deleteButton;

    private final CityDao cityDao = new CityDao();
    private final ObservableList<City> cityList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));
        countryColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getCountry() != null ? cell.getValue().getCountry().getName() : "—"
        ));
        loadCities();
        citiesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            editButton.setDisable(newVal == null);
            deleteButton.setDisable(newVal == null);
        });
    }

    private void loadCities() {
        try {
            cityList.setAll(cityDao.findAll());
            citiesTable.setItems(cityList);
        } catch (Exception e) {
            showError("Fehler beim Laden der Städte: " + e.getMessage());
        }
    }

    @FXML
    public void onAdd() {
        AddEditCityController.showDialog(null, v -> loadCities());
    }

    @FXML
    public void onEdit() {
        City selected = citiesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            AddEditCityController.showDialog(selected, v -> loadCities());
        }
    }

    @FXML
    public void onDelete() {
        City selected = citiesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Stadt wirklich löschen?", ButtonType.YES, ButtonType.NO);
            alert.setHeaderText(null);
            alert.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.YES) {
                    try {
                        cityDao.delete(selected.getId());
                        loadCities();
                    } catch (Exception e) {
                        showError("Fehler beim Löschen: " + e.getMessage());
                    }
                }
            });
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText("Fehler");
        a.showAndWait();
    }
}
