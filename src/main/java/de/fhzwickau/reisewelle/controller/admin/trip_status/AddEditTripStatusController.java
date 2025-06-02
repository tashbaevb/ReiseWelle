package de.fhzwickau.reisewelle.controller.admin.trip_status;

import de.fhzwickau.reisewelle.dao.TripStatusDao;
import de.fhzwickau.reisewelle.model.TripStatus;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class AddEditTripStatusController {

    @FXML
    private TextField nameField;
    @FXML
    private Button onSave, onCancel;

    private final TripStatusDao tripStatusDao = new TripStatusDao();
    private TripStatus tripStatus;
    private Consumer<Void> onSaveCallback;

    public static void showDialog(TripStatus tripStatus, Consumer<Void> onSaveCallback) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(AddEditTripStatusController.class.getResource("/de/fhzwickau/reisewelle/admin/trip_status/add-edit-trip-status.fxml"));
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(tripStatus == null ? "Status hinzufügen" : "Status ändern");
            dialog.setScene(new Scene(loader.load()));
            AddEditTripStatusController controller = loader.getController();
            controller.setTripStatus(tripStatus);
            controller.setOnSaveCallback(onSaveCallback);
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTripStatus(TripStatus tripStatus) {
        this.tripStatus = tripStatus;
        if (tripStatus != null) {
            nameField.setText(tripStatus.getName());
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
            if (tripStatus == null) {
                tripStatusDao.save(new TripStatus(name));
            } else {
                tripStatus.setName(name);
                tripStatusDao.save(tripStatus);
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
