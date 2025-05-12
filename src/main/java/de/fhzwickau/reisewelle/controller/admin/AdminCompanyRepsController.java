package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.model.User;
import de.fhzwickau.reisewelle.repository.UserRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminCompanyRepsController {

    @FXML private TableView<User> companyRepsTable;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> createdAtColumn;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private ObservableList<User> companyReps = FXCollections.observableArrayList();
    private UserRepository userRepository = new UserRepository();

    @FXML
    private void initialize() {
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        createdAtColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCreatedAt() != null ? cellData.getValue().getCreatedAt().toString() : ""));

        companyReps.addAll(userRepository.findCompanyReps());
        companyRepsTable.setItems(companyReps);

        companyRepsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            editButton.setDisable(newSelection == null);
            deleteButton.setDisable(newSelection == null);
        });
    }

    @FXML
    private void addCompanyRep() {
        showAddEditDialog(null);
    }

    @FXML
    private void editCompanyRep() {
        User selected = companyRepsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showAddEditDialog(selected);
        }
    }

    @FXML
    private void deleteCompanyRep() {
        User selected = companyRepsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete this company representative?");
            alert.setContentText("Email: " + selected.getEmail());
            if (alert.showAndWait().get() == ButtonType.OK) {
                userRepository.delete(selected.getId());
                companyReps.remove(selected);
            }
        }
    }

    private void showAddEditDialog(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/fhzwickau/reisewelle/admin/add-edit-user.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(user == null ? "Add Company Representative" : "Edit Company Representative");

            AddEditUserController controller = loader.getController();
            controller.setUser(user);

            stage.setOnHidden(event -> companyReps.setAll(userRepository.findCompanyReps()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}