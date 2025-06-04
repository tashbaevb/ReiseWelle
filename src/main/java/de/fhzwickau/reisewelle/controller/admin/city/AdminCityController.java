package de.fhzwickau.reisewelle.controller.admin.city;

import de.fhzwickau.reisewelle.controller.admin.BaseTableController;
import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.CityDao;
import de.fhzwickau.reisewelle.dao.StopDao;
import de.fhzwickau.reisewelle.model.City;
import de.fhzwickau.reisewelle.model.Stop;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import de.fhzwickau.reisewelle.utils.WindowUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Controller für Verwaltung der Städte
 */
public class AdminCityController extends BaseTableController<City> {

    @FXML
    private TableView<City> citiesTable;
    @FXML
    private TableColumn<City, String> nameColumn;
    @FXML
    private TableColumn<City, String> countryColumn;
    @FXML
    private Button editButton, deleteButton;

    private final BaseDao<City> cityDao = new CityDao();
    private final BaseDao<Stop> stopDao = new StopDao();

    @FXML
    protected void initialize() {
        nameColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));
        countryColumn.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getCountry() != null ? cd.getValue().getCountry().getName() : "—"
        ));

        init(cityDao, citiesTable, editButton, deleteButton);
    }

    @Override
    protected boolean isInUse(City city) {
        try {
            return stopDao.findAll().stream().anyMatch(stop ->
                    stop.getCity() != null && stop.getCity().getId().equals(city.getId()));
        } catch (SQLException e) {
            AlertUtil.showError("Fehler bei Verwendungskontrolle", e.getMessage());
            return true;
        }
    }

    @Override
    protected String getInUseMessage() {
        return "Diese Stadt ist einer oder mehreren Fahrten zugewiesen und kann nicht gelöscht werden.";
    }

    @Override
    protected UUID getId(City city) {
        return city.getId();
    }

    @Override
    protected String getDeleteConfirmationMessage(City city) {
        return "Möchten Sie die Stadt \"" + city.getName() + "\" wirklich löschen?";
    }

    @Override
    protected Stage showAddEditDialog(City city) throws IOException {
        return WindowUtil.showModalWindow(
                "/de/fhzwickau/reisewelle/admin/city/add-edit-city.fxml",
                city == null ? "Stadt hinzufügen" : "Stadt bearbeiten",
                controller -> ((AddEditCityController) controller).setCity(city),
                this::loadDataAsync
        );
    }

    @Override
    protected TableView<City> getTableView() {
        return citiesTable;
    }
}
