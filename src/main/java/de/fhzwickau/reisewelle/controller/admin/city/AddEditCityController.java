package de.fhzwickau.reisewelle.controller.admin.city;

import de.fhzwickau.reisewelle.dao.CityDao;
import de.fhzwickau.reisewelle.dao.CountryDao;
import de.fhzwickau.reisewelle.model.City;
import de.fhzwickau.reisewelle.model.Country;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class AddEditCityController {

    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<Country> countryCombo;
    @FXML
    private Button onSave, onCancel;

    private final CityDao cityDao = new CityDao();
    private final CountryDao countryDao = new CountryDao();
    private City city;
    private Consumer<Void> onSaveCallback;

    public static void showDialog(City city, Consumer<Void> onSaveCallback) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    AddEditCityController.class.getResource("/de/fhzwickau/reisewelle/admin/city/add-edit-city.fxml"));
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(city == null ? "Stadt hinzufügen" : "Stadt ändern");
            dialog.setScene(new Scene(loader.load()));
            AddEditCityController controller = loader.getController();
            controller.setCity(city);
            controller.setOnSaveCallback(onSaveCallback);
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCity(City city) {
        this.city = city;
        try {
            countryCombo.setItems(FXCollections.observableArrayList(countryDao.findAll()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (city != null) {
            nameField.setText(city.getName());
            if (city.getCountry() != null) {
                countryCombo.getSelectionModel().select(city.getCountry());
            }
        } else {
            nameField.setText("");
            countryCombo.getSelectionModel().clearSelection();
        }
    }

    public void setOnSaveCallback(Consumer<Void> callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    public void onSave() {
        String name = nameField.getText().trim();
        Country country = countryCombo.getSelectionModel().getSelectedItem();
        if (name.isEmpty() || country == null) {
            nameField.setStyle("-fx-border-color: red;");
            countryCombo.setStyle("-fx-border-color: red;");
            return;
        }
        try {
            if (city == null) {
                cityDao.add(new City(name, country));
            } else {
                city.setName(name);
                city.setCountry(country);
                cityDao.update(city);
            }
            ((Stage) nameField.getScene().getWindow()).close();
            if (onSaveCallback != null) onSaveCallback.accept(null);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Fehler beim Speichern: " + e.getMessage());
            alert.setHeaderText("Fehler");
            alert.showAndWait();
        }
    }

    @FXML
    public void onCancel() {
        ((Stage) nameField.getScene().getWindow()).close();
    }
}
