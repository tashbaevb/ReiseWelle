package de.fhzwickau.reisewelle.controller.admin;

import de.fhzwickau.reisewelle.dao.*;
import de.fhzwickau.reisewelle.model.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.util.converter.DoubleStringConverter;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class AddEditTripController extends BaseAddEditController<Trip> {

    @FXML private ComboBox<Bus>        busComboBox;
    @FXML private ComboBox<Driver>     driverComboBox;
    @FXML private DatePicker           departureDatePicker;
    @FXML private ComboBox<TripStatus> statusComboBox;

    @FXML private TableView<Stop>               stopsTable;
    @FXML private TableColumn<Stop, Integer>    orderColumn;
    @FXML private TableColumn<Stop, City>       cityColumn;
    @FXML private TableColumn<Stop, LocalDateTime> arrivalColumn;
    @FXML private TableColumn<Stop, LocalDateTime> departureColumn;

    @FXML private TableView<TripStopPrice>         priceTable;
    @FXML private TableColumn<TripStopPrice, City> priceCityColumn;
    @FXML private TableColumn<TripStopPrice, Double> priceColumn;

    private final TripAdminDao      tripDao       = new TripAdminDao();
    private final BusDao            busDao        = new BusDao();
    private final DriverDao         driverDao     = new DriverDao();
    private final TripStatusDao     tripStatusDao = new TripStatusDao();
    private final StopDao           stopDao       = new StopDao();
    private final TripStopPriceDao  priceDao      = new TripStopPriceDao();
    private final CityDao           cityDao       = new CityDao();

    @FXML
    private void initialize() throws SQLException {
        // Load dropdowns
        busComboBox.getItems().setAll(busDao.findAll());
        driverComboBox.getItems().setAll(driverDao.findAll());
        statusComboBox.getItems().setAll(tripStatusDao.findAll());

        // Configure stops table
        orderColumn.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(c.getValue().getStopOrder())
        );
        cityColumn.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(c.getValue().getCity())
        );
        // Mark the start city
        cityColumn.setCellFactory(col -> new TableCell<Stop, City>() {
            @Override
            protected void updateItem(City item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    Stop s = getTableRow().getItem();
                    if (s != null && s.getStopOrder() == 1) {
                        setText(item.getName() + " (Start)");
                    } else {
                        setText(item.getName());
                    }
                }
            }
        });
        arrivalColumn.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(c.getValue().getArrivalTime())
        );
        departureColumn.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(c.getValue().getDepartureTime())
        );
        stopsTable.setItems(FXCollections.observableArrayList());

        // Configure price table
        priceTable.setEditable(true);
        priceCityColumn.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(c.getValue().getStop().getCity())
        );
        priceColumn.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(c.getValue().getPriceFromStart())
        );
        priceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        priceColumn.setOnEditCommit(evt ->
                evt.getRowValue().setPriceFromStart(evt.getNewValue())
        );
        priceTable.setItems(FXCollections.observableArrayList());
    }

    @Override
    protected void saveEntity() throws SQLException {
        // Если это новая сущность — создаём её, иначе — обновляем поля
        if (entity == null) {
            entity = new Trip(
                    busComboBox.getValue(),
                    driverComboBox.getValue(),
                    departureDatePicker.getValue(),
                    statusComboBox.getValue()
            );
        } else {
            entity.setBus(busComboBox.getValue());
            entity.setDriver(driverComboBox.getValue());
            entity.setDepartureDate(departureDatePicker.getValue());
            entity.setStatus(statusComboBox.getValue());
        }

        // 1) Сохраняем или обновляем сам Trip
        tripDao.save(entity);

        // 2) Удаляем все старые записи цен, чтобы не возник конфликтов FK при удалении остановок
        priceDao.deleteAllForTrip(entity.getId());

        // 3) Сохраняем (batch) все остановки для рейса
        int ord = 1;
        for (Stop s : stopsTable.getItems()) {
            s.setTrip(entity);
            s.setStopOrder(ord++);
        }
        stopDao.saveAllForTrip(entity.getId(), stopsTable.getItems());

        // 4) Сохраняем (batch) новые цены
        for (TripStopPrice p : priceTable.getItems()) {
            p.setTrip(entity);
        }
        priceDao.saveAllForTrip(entity.getId(), priceTable.getItems());

        // Закрываем диалог/окно
        close();
    }

    @Override
    protected Node getAnyControl() {
        return busComboBox;
    }

    public void setTrip(Trip trip) throws SQLException {
        this.entity = trip;
        if (trip != null) {
            busComboBox.setValue(trip.getBus());
            driverComboBox.setValue(trip.getDriver());
            departureDatePicker.setValue(trip.getDepartureDate());
            statusComboBox.setValue(trip.getStatus());

            // Load stops
            List<Stop> stops = stopDao.findByTripId(trip.getId());
            stopsTable.setItems(FXCollections.observableArrayList(stops));

            // Load prices and link Trip
            List<TripStopPrice> prices = priceDao.findByTripId(trip.getId());
            prices.forEach(p -> p.setTrip(entity));
            priceTable.setItems(FXCollections.observableArrayList(prices));
        }
    }

    @FXML
    private void addStop() {
        Dialog<Stop> dlg = new Dialog<>();
        dlg.setTitle("Add Stop");
        ButtonType addType = new ButtonType("Add", ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(addType, ButtonType.CANCEL);

        ComboBox<City> cityCombo = new ComboBox<>();
        DatePicker arrDate     = new DatePicker();
        Spinner<Integer> arrHour   = new Spinner<>(0, 23, 12);
        Spinner<Integer> arrMinute = new Spinner<>(0, 59, 0, 1);

        DatePicker depDate     = new DatePicker();
        Spinner<Integer> depHour   = new Spinner<>(0, 23, 12);
        Spinner<Integer> depMinute = new Spinner<>(0, 59, 0, 1);

        try {
            cityCombo.getItems().setAll(cityDao.findAll());
        } catch (SQLException ex) {
            showError("Cannot load cities", ex.getMessage());
            return;
        }

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));
        grid.add(new Label("City:"),           0, 0); grid.add(cityCombo, 1, 0);
        grid.add(new Label("Arrival Date:"),   0, 1); grid.add(arrDate,    1, 1);
        grid.add(new Label("Hour:"),           0, 2); grid.add(arrHour,   1, 2);
        grid.add(new Label("Minute:"),         0, 3); grid.add(arrMinute, 1, 3);
        grid.add(new Label("Departure Date:"), 0, 4); grid.add(depDate,    1, 4);
        grid.add(new Label("Hour:"),           0, 5); grid.add(depHour,   1, 5);
        grid.add(new Label("Minute:"),         0, 6); grid.add(depMinute, 1, 6);

        dlg.getDialogPane().setContent(grid);

        Node addBtn = dlg.getDialogPane().lookupButton(addType);
        BooleanBinding valid = Bindings.createBooleanBinding(() ->
                        cityCombo.getValue() != null
                                && arrDate.getValue() != null
                                && depDate.getValue() != null,
                cityCombo.valueProperty(),
                arrDate.valueProperty(),
                depDate.valueProperty()
        );
        addBtn.disableProperty().bind(valid.not());

        dlg.setResultConverter(btn -> {
            if (btn == addType) {
                LocalDateTime arrival = LocalDateTime.of(
                        arrDate.getValue(),
                        LocalTime.of(arrHour.getValue(), arrMinute.getValue())
                );
                LocalDateTime departure = LocalDateTime.of(
                        depDate.getValue(),
                        LocalTime.of(depHour.getValue(), depMinute.getValue())
                );
                int order = stopsTable.getItems().size() + 1;
                return new Stop(entity, cityCombo.getValue(), arrival, departure, order);
            }
            return null;
        });

        Optional<Stop> res = dlg.showAndWait();
        res.ifPresent(s -> {
            stopsTable.getItems().add(s);
            renumberStops();
            updatePrices();
        });
    }

    @FXML
    private void removeStop() {
        int idx = stopsTable.getSelectionModel().getSelectedIndex();
        if (idx >= 0) {
            stopsTable.getItems().remove(idx);
            renumberStops();
            updatePrices();
        }
    }

    @FXML
    private void moveStopUp() {
        ObservableList<Stop> list = stopsTable.getItems();
        int idx = stopsTable.getSelectionModel().getSelectedIndex();
        if (idx > 0) {
            Stop s = list.remove(idx);
            list.add(idx - 1, s);
            stopsTable.getSelectionModel().select(idx - 1);
            renumberStops();
            updatePrices();
        }
    }

    @FXML
    private void moveStopDown() {
        ObservableList<Stop> list = stopsTable.getItems();
        int idx = stopsTable.getSelectionModel().getSelectedIndex();
        if (idx >= 0 && idx < list.size() - 1) {
            Stop s = list.remove(idx);
            list.add(idx + 1, s);
            stopsTable.getSelectionModel().select(idx + 1);
            renumberStops();
            updatePrices();
        }
    }

    @FXML
    private void markAsStart() {
        Stop sel = stopsTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            ObservableList<Stop> list = stopsTable.getItems();
            list.remove(sel);
            list.add(0, sel);
            renumberStops();
            updatePrices();
        }
    }

    /**
     * Rebuilds the price table for all stops after index 0, preserving any existing values.
     */
    @FXML
    private void updatePrices() {
        Map<UUID, TripStopPrice> existing = priceTable.getItems().stream()
                .collect(Collectors.toMap(p -> p.getStop().getId(), p -> p));

        ObservableList<TripStopPrice> out = FXCollections.observableArrayList();
        List<Stop> stops = stopsTable.getItems();
        for (int i = 1; i < stops.size(); i++) {
            Stop s = stops.get(i);
            TripStopPrice p = existing.get(s.getId());
            if (p == null) {
                p = new TripStopPrice(entity, s, 0.0);
            } else {
                p.setStop(s);
            }
            out.add(p);
        }
        priceTable.setItems(out);
    }

    private void renumberStops() {
        ObservableList<Stop> list = stopsTable.getItems();
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setStopOrder(i + 1);
        }
        stopsTable.refresh();
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
