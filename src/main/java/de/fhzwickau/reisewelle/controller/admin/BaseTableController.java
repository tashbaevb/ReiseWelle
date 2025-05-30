package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.dao.BaseDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public abstract class BaseTableController<T> {

    protected ObservableList<T> items = FXCollections.observableArrayList();

    protected abstract BaseDao<T> getDao();

    protected abstract TableView<T> getTableView();

    protected abstract Object showAddEditDialog(T item) throws IOException;

    protected abstract String getDeleteConfirmationMessage(T item);

    protected Button getEditButton() {
        return null;
    }

    protected abstract Button getDeleteButton();

    protected abstract UUID getId(T entity);

    @FXML
    protected void initialize() throws SQLException {
        List<T> all = getDao().findAll();
        List<T> filtered = applyFilter(all);
        items.setAll(filtered);
        getTableView().setItems(items);

        Button editBtn = getEditButton();
        Button deleteBtn = getDeleteButton();

        getTableView().getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (editBtn != null) editBtn.setDisable(newSelection == null);
            if (deleteBtn != null) deleteBtn.setDisable(newSelection == null);
        });
    }

    @FXML
    protected void onAdd() throws IOException {
        showAddEditDialog(null);
    }

    @FXML
    protected void onEdit() throws IOException {
        T selected = getTableView().getSelectionModel().getSelectedItem();
        if (selected != null) {
            showAddEditDialog(selected);
        }
    }

    @FXML
    protected void onDelete() throws SQLException {
        T selected = getTableView().getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Bestätigung");
            alert.setHeaderText("Möchten Sie wirklich löschen?");
            alert.setContentText(getDeleteConfirmationMessage(selected));

            if (alert.showAndWait().get() == ButtonType.OK) {
                getDao().delete(getId(selected));
                items.remove(selected);
            }
        }
    }

    protected void refreshData() {
        try {
            List<T> all = getDao().findAll();
            List<T> filtered = applyFilter(all);
            items.setAll(filtered);
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog();
        }
    }

    protected List<T> applyFilter(List<T> items) {
        return items;
    }

    protected void showErrorDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setHeaderText(null);
        alert.setContentText("Daten konnten nicht neu geladen werden");
        alert.showAndWait();
    }
}
