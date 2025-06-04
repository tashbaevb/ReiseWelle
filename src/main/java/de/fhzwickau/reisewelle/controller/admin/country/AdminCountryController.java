package de.fhzwickau.reisewelle.controller.admin.country;

import de.fhzwickau.reisewelle.controller.admin.BaseTableController;
import de.fhzwickau.reisewelle.dao.CountryDao;
import de.fhzwickau.reisewelle.model.Country;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class AdminCountryController extends BaseTableController<Country> {

    @FXML
    private TableView<Country> countriesTable;
    @FXML
    private TableColumn<Country, String> nameColumn;
    @FXML
    private Button addButton, editButton, deleteButton;

    private final CountryDao countryDao = new CountryDao();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        init(countryDao, countriesTable, editButton, deleteButton);

        addButton.setOnAction(e -> onAdd());
    }

    @Override
    protected boolean isInUse(Country entity) {
        return false;
    }

    @Override
    protected String getInUseMessage() {
        return "Das Land wird verwendet und kann nicht gelöscht werden.";
    }

    @Override
    protected UUID getId(Country entity) {
        return entity.getId();
    }

    @Override
    protected String getDeleteConfirmationMessage(Country entity) {
        return "Land: " + entity.getName() + " wirklich löschen?";
    }

    @Override
    protected Stage showAddEditDialog(Country entity) throws IOException {
        return de.fhzwickau.reisewelle.utils.WindowUtil.showModalWindow(
                "/de/fhzwickau/reisewelle/admin/country/add-edit-country.fxml",
                entity == null ? "Land hinzufügen" : "Land bearbeiten",
                controller -> {
                    AddEditCountryController c = (AddEditCountryController) controller;
                    c.setCountry(entity);
                    c.setOnSaved(v -> loadDataAsync());
                },
                this::loadDataAsync
        );
    }


    @Override
    protected TableView<Country> getTableView() {
        return countriesTable;
    }

    @Override
    @FXML
    protected void onDelete() {
        try {
            super.onDelete();
        } catch (SQLException e) {
            AlertUtil.showError("Fehler beim Löschen", e.getMessage());
        }
    }
}
