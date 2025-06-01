package de.fhzwickau.reisewelle.controller.admin;

import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.sql.SQLException;

public abstract class BaseAddEditController<T> {

    protected T entity;

    protected abstract void saveEntity() throws SQLException;

    protected abstract javafx.scene.Node getAnyControl();

    @FXML
    protected void save() throws SQLException {
        saveEntity();
        close();
    }

    @FXML
    protected void cancel() {
        close();
    }

    protected void close() {
        Stage stage = (Stage) getAnyControl().getScene().getWindow();
        stage.close();
    }
}
