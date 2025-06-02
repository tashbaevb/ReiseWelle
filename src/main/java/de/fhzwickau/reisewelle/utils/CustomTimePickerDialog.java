package de.fhzwickau.reisewelle.utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.time.LocalTime;
import java.util.Optional;

public class CustomTimePickerDialog {

    public static Optional<LocalTime> showAndWait() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Zeit wählen");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(18));
        grid.setAlignment(Pos.CENTER);

        Text label = new Text("Stunde wählen");
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        grid.add(label, 0, 0, 6, 1);

        // Часы: 0..23
        int col = 0, row = 1;
        for (int h = 0; h < 24; h++) {
            Button btn = new Button(String.format("%02d", h));
            btn.setPrefWidth(38);
            btn.setPrefHeight(38);
            btn.setStyle("-fx-background-radius: 10; -fx-font-size: 14px; -fx-font-weight: bold;");
            int finalH = h;
            btn.setOnAction(e -> {
                dialog.setUserData(finalH);
                dialog.close();
            });
            grid.add(btn, col, row);
            col++;
            if (col == 6) { col = 0; row++; }
        }

        Scene scene = new Scene(grid);
        dialog.setScene(scene);
        dialog.setWidth(320);
        dialog.setHeight(270);
        dialog.showAndWait();

        // Проверим, что юзер выбрал час
        Object hourObj = dialog.getUserData();
        if (hourObj == null) return Optional.empty();
        int hour = (int) hourObj;

        // Открываем минуты
        Stage minDialog = new Stage();
        minDialog.initModality(Modality.APPLICATION_MODAL);
        minDialog.setTitle("Minuten wählen");

        GridPane minGrid = new GridPane();
        minGrid.setHgap(12);
        minGrid.setVgap(12);
        minGrid.setPadding(new Insets(18));
        minGrid.setAlignment(Pos.CENTER);

        Text minLabel = new Text("Minute wählen");
        minLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        minGrid.add(minLabel, 0, 0, 6, 1);

        col = 0; row = 1;
        for (int m = 0; m < 60; m += 5) {
            Button btn = new Button(String.format("%02d", m));
            btn.setPrefWidth(38);
            btn.setPrefHeight(38);
            btn.setStyle("-fx-background-radius: 10; -fx-font-size: 14px; -fx-font-weight: bold;");
            int finalM = m;
            btn.setOnAction(e -> {
                minDialog.setUserData(finalM);
                minDialog.close();
            });
            minGrid.add(btn, col, row);
            col++;
            if (col == 6) { col = 0; row++; }
        }

        minDialog.setScene(new Scene(minGrid));
        minDialog.setWidth(320);
        minDialog.setHeight(270);
        minDialog.showAndWait();

        Object minObj = minDialog.getUserData();
        if (minObj == null) return Optional.empty();
        int minute = (int) minObj;

        return Optional.of(LocalTime.of(hour, minute));
    }
}
