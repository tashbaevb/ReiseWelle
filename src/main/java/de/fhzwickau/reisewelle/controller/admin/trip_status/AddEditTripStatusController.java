package de.fhzwickau.reisewelle.controller.admin.trip_status;

import de.fhzwickau.reisewelle.controller.admin.BaseAddEditController;
import de.fhzwickau.reisewelle.dao.TripStatusDao;
import de.fhzwickau.reisewelle.model.TripStatus;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import de.fhzwickau.reisewelle.utils.WindowUtil;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

public class AddEditTripStatusController extends BaseAddEditController<TripStatus> {

    @FXML
    private TextField nameField;

    private final TripStatusDao tripStatusDao = new TripStatusDao();

    private Runnable onSaved;

    public void setTripStatus(TripStatus tripStatus) {
        this.entity = tripStatus;
        if (entity != null) {
            nameField.setText(entity.getName());
        } else {
            nameField.clear();
        }
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    @Override
    protected void saveEntity() throws SQLException {
        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            AlertUtil.showError("Fehler", "Name darf nicht leer sein");
            throw new SQLException("Name darf nicht leer sein"); // прервать сохранение
        }

        if (entity == null) {
            entity = new TripStatus(name);
        } else {
            entity.setName(name);
        }

        tripStatusDao.save(entity);

        if (onSaved != null) {
            onSaved.run();
        }
    }

    @Override
    protected Node getAnyControl() {
        return nameField;
    }

    public static void showDialog(TripStatus tripStatus, Runnable onSaved) {
        try {
            WindowUtil.showModalWindow(
                    "/de/fhzwickau/reisewelle/admin/trip_status/add-edit-trip-status.fxml",
                    tripStatus == null ? "Status hinzufügen" : "Status bearbeiten",
                    controller -> {
                        AddEditTripStatusController c = (AddEditTripStatusController) controller;
                        c.setTripStatus(tripStatus);
                        c.setOnSaved(onSaved);
                    },
                    onSaved
            );
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Fehler beim Öffnen des Dialogs", e.getMessage());
        }
    }
}
