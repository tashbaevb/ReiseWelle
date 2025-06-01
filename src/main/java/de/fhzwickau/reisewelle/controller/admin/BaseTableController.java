package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class BaseTableController<T> {

    protected BaseDao<T> dao;
    protected TableView<T> tableView;
    protected Button editButton;
    protected Button deleteButton;

    protected ObservableList<T> items = FXCollections.observableArrayList();

    protected void init(BaseDao<T> dao, TableView<T> tableView, Button editButton, Button deleteButton) {
        this.dao = dao;
        this.tableView = tableView;
        this.editButton = editButton;
        this.deleteButton = deleteButton;

        this.tableView.setItems(items);
        editButton.setDisable(true);
        deleteButton.setDisable(true);

        this.tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean disable = newSel == null;
            editButton.setDisable(disable);
            deleteButton.setDisable(disable);
        });

        loadDataAsync();
    }

    protected void loadDataAsync() {
        Task<List<T>> task = new Task<>() {
            @Override
            protected List<T> call() throws Exception {
                return dao.findAll();
            }
        };
        task.setOnSucceeded(e -> {
            items.setAll(task.getValue());
        });
        task.setOnFailed(e -> {
            AlertUtil.showError("Fehler beim Laden", task.getException().getMessage());
        });
        new Thread(task, "LoadDataThread").start();
    }

    @FXML
    protected void onAdd() {
        try {
            showAddEditDialog(null);
        } catch (IOException e) {
            AlertUtil.showError("Fehler beim Öffnen des Dialogfelds „Hinzufügen“", e.getMessage());
        }
    }

    @FXML
    protected void onEdit() {
        T selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                showAddEditDialog(selected);
            } catch (IOException e) {
                AlertUtil.showError("Fehler beim Öffnen des Dialogfelds „Ändern“", e.getMessage());
            }
        }
    }

    @FXML
    protected void onDelete() throws SQLException {
        T selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (isInUse(selected)) {
            AlertUtil.showError("Element kann nicht gelöscht werden", getInUseMessage());
            return;
        }

        boolean confirmed = AlertUtil.showConfirmation("Löschen bestätigen", getDeleteConfirmationMessage(selected));
        if (confirmed) {
            try {
                dao.delete(getId(selected));
                loadDataAsync();
            } catch (SQLException ex) {
                AlertUtil.showError("Fehler beim Löschen", ex.getMessage());
            }
        }
    }

    protected abstract boolean isInUse(T entity);

    protected abstract String getInUseMessage();

    protected abstract UUID getId(T entity);

    protected abstract String getDeleteConfirmationMessage(T entity);

    protected abstract Stage showAddEditDialog(T entity) throws IOException;

    protected abstract TableView<T> getTableView();
}
