package de.fhzwickau.reisewelle.controller.admin.country;

import de.fhzwickau.reisewelle.dao.CountryDao;
import de.fhzwickau.reisewelle.model.Country;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class AddEditCountryController {

    @FXML
    private TextField nameField;
    @FXML
    private Button onSave, onCancel;

    private CountryDao countryDao = new CountryDao();
    private Country country;
    private Consumer<Void> onSaveCallback;

    public static void showDialog(Country country, Consumer<Void> onSaveCallback) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(AddEditCountryController.class.getResource("/de/fhzwickau/reisewelle/admin/country/add-edit-country.fxml"));
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(country == null ? "Land hinzufügen" : "Land ändern");
            dialog.setScene(new Scene(loader.load()));
            AddEditCountryController controller = loader.getController();
            controller.setCountry(country);
            controller.setOnSaveCallback(onSaveCallback);
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCountry(Country country) {
        this.country = country;
        if (country != null) {
            nameField.setText(country.getName());
        }
    }

    public void setOnSaveCallback(Consumer<Void> callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    public void onSave() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            nameField.setStyle("-fx-border-color: red;");
            return;
        }
        try {
            if (country == null) {
                countryDao.save(new Country(name));
            } else {
                country.setName(name);
                countryDao.update(country);
            }
            ((Stage) nameField.getScene().getWindow()).close();
            if (onSaveCallback != null) onSaveCallback.accept(null);
        } catch (Exception e) {
            e.printStackTrace();
            nameField.setStyle("-fx-border-color: red;");
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
