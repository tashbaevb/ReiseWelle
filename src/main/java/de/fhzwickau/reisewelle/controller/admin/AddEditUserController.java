package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.model.User;
import de.fhzwickau.reisewelle.model.UserRole;
import de.fhzwickau.reisewelle.repository.UserRepository;
import de.fhzwickau.reisewelle.repository.UserRoleRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;

public class AddEditUserController {

    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private ComboBox<UserRole> roleComboBox;
    @FXML private Button saveButton;

    private User user;
    private UserRepository userRepository = new UserRepository();
    private UserRoleRepository userRoleRepository = new UserRoleRepository();
    private Stage stage;

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            emailField.setText(user.getEmail());
            passwordField.setText(user.getPassword());
            roleComboBox.setValue(user.getUserRole());
        }
    }

    @FXML
    private void initialize() {
        List<UserRole> roles = userRoleRepository.findAll();
        System.out.println("Loaded roles count: " + roles.size());
        for (UserRole role : roles) {
            System.out.println("Loaded role: id=" + role.getId() + ", name=" + role.getRoleName());
        }
        roleComboBox.getItems().setAll(roles);

        saveButton.setOnAction(event -> {
            if (validateInput()) {
                if (user == null) {
                    user = new User();
                }
                user.setEmail(emailField.getText());
                user.setPassword(passwordField.getText());
                UserRole selectedRole = roleComboBox.getValue();
                if (selectedRole != null) {
                    user.setUserRole(selectedRole);
                    System.out.println("Saving user: email=" + user.getEmail() + ", role=" + selectedRole.getRoleName());
                } else {
                    System.out.println("Error: No role selected");
                    return;
                }
                userRepository.save(user);
                stage.close();
            }
        });
    }

    private boolean validateInput() {
        String email = emailField.getText();
        String password = passwordField.getText();
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty() || roleComboBox.getValue() == null) {
            System.out.println("Validation failed: Empty fields or no role selected");
            return false;
        }
        return true;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}