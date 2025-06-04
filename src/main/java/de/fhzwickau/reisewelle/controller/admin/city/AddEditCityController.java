package de.fhzwickau.reisewelle.controller.admin.city;

import de.fhzwickau.reisewelle.controller.admin.BaseAddEditController;
import de.fhzwickau.reisewelle.dao.CityDao;
import de.fhzwickau.reisewelle.dao.CountryDao;
import de.fhzwickau.reisewelle.model.City;
import de.fhzwickau.reisewelle.model.Country;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.function.Consumer;

public class AddEditCityController extends BaseAddEditController<City> {

    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<Country> countryCombo;

    private final CityDao cityDao = new CityDao();
    private final CountryDao countryDao = new CountryDao();
    private Consumer<Void> onSaveCallback;

    public static void showDialog(City city, Consumer<Void> onSaveCallback) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    AddEditCityController.class.getResource("/de/fhzwickau/reisewelle/admin/city/add-edit-city.fxml"));
            Stage dialog = new Stage();
            dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialog.setTitle(city == null ? "Stadt hinzufügen" : "Stadt ändern");
            dialog.setScene(new javafx.scene.Scene(loader.load()));
            AddEditCityController controller = loader.getController();
            controller.setCity(city);
            controller.setOnSaveCallback(onSaveCallback);
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCity(City city) {
        this.entity = city;
        try {
            countryCombo.setItems(FXCollections.observableArrayList(countryDao.findAll()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (entity != null) {
            nameField.setText(entity.getName());
            if (entity.getCountry() != null) {
                countryCombo.getSelectionModel().select(entity.getCountry());
            }
        } else {
            nameField.setText("");
            countryCombo.getSelectionModel().clearSelection();
        }
    }

    public void setOnSaveCallback(Consumer<Void> onSaveCallback) {
        this.onSaveCallback = onSaveCallback;
    }

    @Override
    protected void saveEntity() throws SQLException {
        String name = nameField.getText().trim();
        Country country = countryCombo.getSelectionModel().getSelectedItem();

        if (name.isEmpty() || country == null) {
            throw new SQLException("Name und Land müssen ausgefüllt sein.");
        }

        if (entity == null) {
            entity = new City(name, country);
            cityDao.add(entity);
        } else {
            entity.setName(name);
            entity.setCountry(country);
            cityDao.update(entity);
        }

        if (onSaveCallback != null) onSaveCallback.accept(null);
    }

    @Override
    protected javafx.scene.Node getAnyControl() {
        return nameField;
    }

    @FXML
    protected void save() {
        try {
            saveEntity();
            close();
        } catch (SQLException e) {
            AlertUtil.showError("Fehler beim Speichern", e.getMessage());
            if (nameField.getText().trim().isEmpty()) {
                nameField.setStyle("-fx-border-color: red;");
            } else {
                nameField.setStyle(null);
            }
            if (countryCombo.getSelectionModel().getSelectedItem() == null) {
                countryCombo.setStyle("-fx-border-color: red;");
            } else {
                countryCombo.setStyle(null);
            }
        }
    }

    @FXML
    protected void cancel() {
        close();
    }
}
