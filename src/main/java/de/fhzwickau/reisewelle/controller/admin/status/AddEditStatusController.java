package de.fhzwickau.reisewelle.controller.admin.status;

import de.fhzwickau.reisewelle.dao.StatusDao;
import de.fhzwickau.reisewelle.model.Status;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class AddEditStatusController {

    @FXML
    private TextField nameField;
    @FXML
    private Button onSave, onCancel;

    private final StatusDao statusDao = new StatusDao();
    private Status status;
    private Consumer<Void> onSaveCallback;

    public static void showDialog(Status status, Consumer<Void> onSaveCallback) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(AddEditStatusController.class.getResource("/de/fhzwickau/reisewelle/admin/status/add-edit-status.fxml"));
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(status == null ? "Status hinzufügen" : "Status ändern");
            dialog.setScene(new Scene(loader.load()));
            AddEditStatusController controller = loader.getController();
            controller.setStatus(status);
            controller.setOnSaveCallback(onSaveCallback);
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStatus(Status status) {
        this.status = status;
        if (status != null) {
            nameField.setText(status.getName());
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
            if (status == null) {
                statusDao.save(new Status(name));
            } else {
                status.setName(name);
                statusDao.save(status);
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
