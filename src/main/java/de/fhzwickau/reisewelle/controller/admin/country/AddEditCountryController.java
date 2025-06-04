package de.fhzwickau.reisewelle.controller.admin.country;

import de.fhzwickau.reisewelle.controller.admin.BaseAddEditController;
import de.fhzwickau.reisewelle.dao.CountryDao;
import de.fhzwickau.reisewelle.model.Country;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import de.fhzwickau.reisewelle.utils.WindowUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.function.Consumer;

public class AddEditCountryController extends BaseAddEditController<Country> {

    @FXML
    private TextField nameField;

    private final CountryDao countryDao = new CountryDao();
    private Consumer<Void> onSavedCallback;

    public void setCountry(Country country) {
        this.entity = country;
        if (entity != null) {
            nameField.setText(entity.getName());
        } else {
            nameField.setText("");
        }
    }

    public void setOnSaved(Consumer<Void> callback) {
        this.onSavedCallback = callback;
    }

    @Override
    protected void saveEntity() throws SQLException {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            throw new SQLException("Der Name darf nicht leer sein.");
        }

        if (entity == null) {
            entity = new Country(name);
            countryDao.save(entity);
        } else {
            entity.setName(name);
            countryDao.update(entity);
        }

        if (onSavedCallback != null) {
            onSavedCallback.accept(null);
        }
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
        }
    }

    @FXML
    protected void cancel() {
        close();
    }

    public static Stage showDialog(Country country, Consumer<Void> onSaved) throws Exception {
        return WindowUtil.showModalWindow(
                "/de/fhzwickau/reisewelle/admin/country/add-edit-country.fxml",
                country == null ? "Land hinzufügen" : "Land bearbeiten",
                controller -> {
                    AddEditCountryController c = (AddEditCountryController) controller;
                    c.setCountry(country);
                    c.setOnSaved(onSaved);
                },
                () -> onSaved.accept(null)
        );
    }
}
