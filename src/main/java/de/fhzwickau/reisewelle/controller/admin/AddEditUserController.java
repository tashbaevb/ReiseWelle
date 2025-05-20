package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.model.User;
import de.fhzwickau.reisewelle.model.UserRole;
import de.fhzwickau.reisewelle.dao.UserRepository;
import de.fhzwickau.reisewelle.dao.UserRoleRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setRoleFilter(UUID allowedRoleId) {
        // Фильтруем роли, оставляем только ту, что соответствует allowedRoleId
        List<UserRole> roles = userRoleRepository.findAll().stream()
                .filter(role -> role.getId().equals(allowedRoleId))
                .toList();
        roleComboBox.getItems().setAll(roles);
        if (!roles.isEmpty()) {
            roleComboBox.setValue(roles.get(0)); // Устанавливаем роль по умолчанию
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
    }

    @FXML
    private void save() {
        if (validateInput()) {
            if (user == null) {
                String email = emailField.getText();
                String password = passwordField.getText();
                UserRole selectedRole = roleComboBox.getValue();
                if (selectedRole == null) {
                    System.out.println("Error: No role selected");
                    return;
                }
                user = new User(email, password, selectedRole, LocalDateTime.now());
                System.out.println("Saving new user: email=" + user.getEmail() + ", role=" + user.getUserRole().getRoleName());
            } else {
                user.setEmail(emailField.getText());
                user.setPassword(passwordField.getText());
                UserRole selectedRole = roleComboBox.getValue();
                if (selectedRole != null) {
                    user.setUserRole(selectedRole);
                }
                System.out.println("Updating user: email=" + user.getEmail() + ", role=" + user.getUserRole().getRoleName());
            }
            userRepository.save(user);
            stage.close();
        }
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

    @FXML
    private void cancel() {
        stage.close();
    }
}