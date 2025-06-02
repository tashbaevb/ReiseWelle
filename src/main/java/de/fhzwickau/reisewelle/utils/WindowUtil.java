package de.fhzwickau.reisewelle.utils;

import de.fhzwickau.reisewelle.ReiseWelleApplication;
import de.fhzwickau.reisewelle.config.AccessManager;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

public class WindowUtil {

    public static void openWindow(String fxmlPath, String title, Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(ReiseWelleApplication.class.getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            Stage newStage = new Stage();
            newStage.setTitle(title);
            newStage.setScene(scene);
            newStage.show();

            // Закрываем текущее окно только если event не null
            if (event != null && event.getSource() instanceof Node node) {
                Stage currentStage = (Stage) node.getScene().getWindow();
                currentStage.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static <T> Stage showModalWindow(String fxmlPath, String title, Consumer<T> controllerSetup, Runnable onClose) throws IOException {
        FXMLLoader loader = new FXMLLoader(WindowUtil.class.getResource(fxmlPath));
        Parent root = loader.load();
        T controller = loader.getController();

        if (controllerSetup != null) controllerSetup.accept(controller);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        if (onClose != null) stage.setOnHidden(e -> onClose.run());
        stage.show();
        return stage;
    }
}
