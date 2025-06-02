package de.fhzwickau.reisewelle.controller.admin.country;

import de.fhzwickau.reisewelle.dao.CountryDao;
import de.fhzwickau.reisewelle.model.Country;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdminCountryController {

    @FXML
    private TableView<Country> countriesTable;
    @FXML
    private TableColumn<Country, String> nameColumn;
    @FXML
    private Button addButton, editButton, deleteButton;

    private final CountryDao countryDao = new CountryDao();
    private final ObservableList<Country> countryList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));
        loadCountries();
        countriesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            editButton.setDisable(newVal == null);
            deleteButton.setDisable(newVal == null);
        });
    }

    private void loadCountries() {
        try {
            countryList.setAll(countryDao.findAll());
            countriesTable.setItems(countryList);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Fehler beim Laden der Länder: " + e.getMessage());
        }
    }

    @FXML
    public void onAdd() {
        AddEditCountryController.showDialog(null, v -> loadCountries());
    }

    @FXML
    public void onEdit() {
        Country selected = countriesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            AddEditCountryController.showDialog(selected, v -> loadCountries());
        }
    }

    @FXML
    public void onDelete() {
        Country selected = countriesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Land wirklich löschen?", ButtonType.YES, ButtonType.NO);
            alert.setHeaderText(null);
            alert.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.YES) {
                    try {
                        countryDao.delete(selected.getId());
                        loadCountries();
                    } catch (Exception e) {
                        e.printStackTrace();
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
