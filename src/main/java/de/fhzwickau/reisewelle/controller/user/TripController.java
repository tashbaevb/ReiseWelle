package de.fhzwickau.reisewelle.controller.user;

import de.fhzwickau.reisewelle.dto.TripSegmentDTO;
import de.fhzwickau.reisewelle.dao.TripDao;
import de.fhzwickau.reisewelle.utils.CustomDateTimePicker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class TripController implements Initializable {

    private CustomDateTimePicker dateTimePicker;

    @FXML private TextField fromCityField;
    @FXML private TextField toCityField;
    @FXML private Spinner<Integer> adultSpinner;
    @FXML private Spinner<Integer> childSpinner;
    @FXML private Spinner<Integer> bikeSpinner;
    @FXML private AnchorPane datetimePickerContainer;

    @FXML private TableView<TripSegmentDTO> tripTable;
    @FXML private TableColumn<TripSegmentDTO, String> fromColumn;
    @FXML private TableColumn<TripSegmentDTO, String> toColumn;
    @FXML private TableColumn<TripSegmentDTO, LocalDateTime> departureColumn;
    @FXML private TableColumn<TripSegmentDTO, LocalDateTime> arrivalColumn;
    @FXML private TableColumn<TripSegmentDTO, Double> priceColumn;

    private final TripDao tripRepo = new TripDao();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("fromCity"));
        toColumn.setCellValueFactory(new PropertyValueFactory<>("toCity"));
        departureColumn.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        arrivalColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        adultSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 40, 1));
        childSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 40, 0));
        bikeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0));

        CustomDateTimePicker dateTimePicker = new CustomDateTimePicker();
        dateTimePicker.setPrefWidth(250);
        datetimePickerContainer.getChildren().add(dateTimePicker);
        AnchorPane.setTopAnchor(dateTimePicker, 0.0);
        AnchorPane.setRightAnchor(dateTimePicker, 0.0);
        AnchorPane.setBottomAnchor(dateTimePicker, 0.0);
        AnchorPane.setLeftAnchor(dateTimePicker, 0.0);

        this.dateTimePicker = dateTimePicker;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        departureColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.format(formatter));
            }
        });

        arrivalColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.format(formatter));
            }
        });
        tripTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    @FXML
    public void onSearchClicked() throws SQLException {
        String from = fromCityField.getText();
        String to = toCityField.getText();
        LocalDateTime dateTime = dateTimePicker.getDateTimeValue();

        int adults = adultSpinner.getValue();
        int children = childSpinner.getValue();
        int bikes = bikeSpinner.getValue();

        List<TripSegmentDTO> trips = tripRepo.searchTrips(from, to, dateTime, adults, children, bikes);
        tripTable.getItems().setAll(trips);
    }


    @FXML
    public void onTripSelected() throws IOException, SQLException {
        TripSegmentDTO selected = tripTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/fhzwickau/reisewelle/user/detail_trip.fxml"));
            Parent root = loader.load();
            DetailTripController controller = loader.getController();
            controller.loadTripDetails(selected.getTripId());
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        }
    }
}
