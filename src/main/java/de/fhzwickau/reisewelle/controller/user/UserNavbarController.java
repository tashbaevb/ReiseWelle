package de.fhzwickau.reisewelle.controller.user;

import javafx.fxml.FXML;
import de.fhzwickau.reisewelle.controller.user.UserMainController;

public class UserNavbarController {

    // Главный контроллер задаётся извне
    private UserMainController mainController;

    public void setMainController(UserMainController controller) {
        this.mainController = controller;
    }

    @FXML
    public void goTrips() {
        if (mainController != null)
            mainController.loadContent("/de/fhzwickau/reisewelle/user/trips_page.fxml");
    }

    @FXML
    public void goProfile() {
        if (mainController != null)
            mainController.loadContent("/de/fhzwickau/reisewelle/user/user-profile-page.fxml");
    }

    @FXML
    public void logout() {
        mainController.logoutToLogin();
    }
}
