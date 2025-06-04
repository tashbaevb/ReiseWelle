package de.fhzwickau.reisewelle.controller.admin.status;

import de.fhzwickau.reisewelle.controller.admin.BaseAddEditController;
import de.fhzwickau.reisewelle.dao.StatusDao;
import de.fhzwickau.reisewelle.model.Status;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.function.Consumer;

public class AddEditStatusController extends BaseAddEditController<Status> {

    @FXML
    private TextField nameField;

    private final StatusDao statusDao = new StatusDao();
    private Consumer<Void> onSaveCallback;

    public static Stage showDialog(Status status, Consumer<Void> onSaveCallback) {
        try {
            FXMLLoader loader = new FXMLLoader(AddEditStatusController.class.getResource("/de/fhzwickau/reisewelle/admin/status/add-edit-status.fxml"));
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(status == null ? "Status hinzufügen" : "Status ändern");
            dialog.setScene(new Scene(loader.load()));

            AddEditStatusController controller = loader.getController();
            controller.setStatus(status);
            controller.setOnSaveCallback(onSaveCallback);

            dialog.showAndWait();
            return dialog;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setStatus(Status status) {
        this.entity = status;
        if (status != null) {
            nameField.setText(status.getName());
        }
    }

    public void setOnSaveCallback(Consumer<Void> callback) {
        this.onSaveCallback = callback;
    }

    @Override
    protected void saveEntity() throws SQLException {
        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            nameField.setStyle("-fx-border-color: red;");
            throw new IllegalArgumentException("Name darf nicht leer sein");
        }

        try {
            if (entity == null) {
                statusDao.save(new Status(name));
            } else {
                entity.setName(name);
                statusDao.save(entity);
            }

            if (onSaveCallback != null) onSaveCallback.accept(null);
        } catch (Exception e) {
            e.printStackTrace();
            nameField.setStyle("-fx-border-color: red;");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Fehler beim Speichern: " + e.getMessage());
            alert.setHeaderText("Fehler");
            alert.showAndWait();
            throw new SQLException("Speichern fehlgeschlagen", e);
        }
    }

    @Override
    protected Node getAnyControl() {
        return nameField;
    }

    @FXML
    public void onSave() {
        try {
            save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onCancel() {
        cancel();
    }
}
