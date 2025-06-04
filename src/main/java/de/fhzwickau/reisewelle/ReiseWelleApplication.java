package de.fhzwickau.reisewelle;

import de.fhzwickau.reisewelle.config.JDBCConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ReiseWelleApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ReiseWelleApplication.class.getResource("login-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Wilkommen in Reise Welle!");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        JDBCConfig.close();
    }

    public static void main(String[] args) {
        launch();
    }
}