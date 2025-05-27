package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.dao.UserDao;
import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AdminUserController extends BaseTableController<User> {

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> createdAtColumn;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private final UserDao userDao = new UserDao();

    @FXML
    @Override
    protected void initialize() throws SQLException {
        // set up columns
        emailColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getEmail())
        );
        roleColumn.setCellValueFactory(c -> {
            var r = c.getValue().getUserRole();
            return new SimpleStringProperty(r == null ? "" : r.getRoleName());
        });
        createdAtColumn.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getCreatedAt() == null
                                ? ""
                                : c.getValue().getCreatedAt().toString()
                )
        );

        // let the base class do the rest (wiring up the table, selection listeners, etc)
        super.initialize();
    }

    @Override
    protected BaseDao<User> getDao() {
        return userDao;
    }

    @Override
    protected TableView<User> getTableView() {
        return usersTable;
    }

    @Override
    protected Button getEditButton() {
        return editButton;
    }

    @Override
    protected Button getDeleteButton() {
        return deleteButton;
    }

    @Override
    protected UUID getId(User user) {
        return user.getId();
    }

    @Override
    protected String getDeleteConfirmationMessage(User user) {
        return "Delete passenger: " + user.getEmail() + "?";
    }

    @Override
    protected Stage showAddEditDialog(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/de/fhzwickau/reisewelle/admin/add-edit-user.fxml")
        );
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(user == null ? "Add Passenger" : "Edit Passenger");

        AddEditUserController ctrl = loader.getController();
        ctrl.setUser(user);

        // refresh when dialog closes
        stage.setOnHidden(e -> refreshData());
        stage.show();

        return stage;
    }

    @Override
    protected List<User> applyFilter(List<User> all) {
        // show only those whose role name is "passenger"
        return all.stream()
                .filter(u -> u.getUserRole() != null
                        && "passenger".equalsIgnoreCase(u.getUserRole().getRoleName()))
                .collect(Collectors.toList());
    }
}
