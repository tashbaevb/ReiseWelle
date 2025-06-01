package de.fhzwickau.reisewelle.controller.admin.trip;

import de.fhzwickau.reisewelle.controller.admin.BaseAddEditController;
import de.fhzwickau.reisewelle.dao.BaseDao;
import de.fhzwickau.reisewelle.dao.BusDao;
import de.fhzwickau.reisewelle.dao.CityDao;
import de.fhzwickau.reisewelle.dao.DriverDao;
import de.fhzwickau.reisewelle.dao.SeatAvailabilityDao;
import de.fhzwickau.reisewelle.dao.StopDao;
import de.fhzwickau.reisewelle.dao.TicketDao;
import de.fhzwickau.reisewelle.dao.TripAdminDao;
import de.fhzwickau.reisewelle.dao.TripStatusDao;
import de.fhzwickau.reisewelle.dao.TripStopPriceDao;
import de.fhzwickau.reisewelle.model.Bus;
import de.fhzwickau.reisewelle.model.City;
import de.fhzwickau.reisewelle.model.Driver;
import de.fhzwickau.reisewelle.model.SeatAvailability;
import de.fhzwickau.reisewelle.model.Stop;
import de.fhzwickau.reisewelle.model.Trip;
import de.fhzwickau.reisewelle.model.TripStatus;
import de.fhzwickau.reisewelle.model.TripStopPrice;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import de.fhzwickau.reisewelle.utils.FormValidator;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

    @FXML
    private ComboBox<Bus> busComboBox;
    @FXML
    private ComboBox<Driver> driverComboBox;
    @FXML
    private DatePicker departureDatePicker;
    @FXML
    private ComboBox<TripStatus> statusComboBox;

    @FXML
    private TableView<Stop> stopsTable;
    @FXML
    private TableColumn<Stop, Integer> orderColumn;
    @FXML
    private TableColumn<Stop, City> cityColumn;
    @FXML
    private TableColumn<Stop, LocalDateTime> arrivalColumn, departureColumn;

    @FXML
    private TableView<TripStopPrice> priceTable;
    @FXML
    private TableColumn<TripStopPrice, City> priceCityColumn;

    @FXML
    private TableColumn<TripStopPrice, Double> priceAdultColumn, priceChildColumn;

    private final BaseDao<Trip> tripDao = new TripAdminDao();
    private final BaseDao<Bus> busDao = new BusDao();
    private final BaseDao<Driver> driverDao = new DriverDao();
    private final BaseDao<TripStatus> tripStatusDao = new TripStatusDao();
    private final StopDao stopDao = new StopDao();
    private final TripStopPriceDao priceDao = new TripStopPriceDao();
    private final BaseDao<City> cityDao = new CityDao();
    private final TicketDao ticketDao = new TicketDao();
    private final SeatAvailabilityDao seatAvailabilityDao = new SeatAvailabilityDao();

    @FXML
    private void initialize() throws SQLException {
        busComboBox.getItems().setAll(busDao.findAll());
        driverComboBox.getItems().setAll(driverDao.findAll());
        statusComboBox.getItems().setAll(tripStatusDao.findAll());

        orderColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getStopOrder()));
        cityColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCity()));

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
        arrivalColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getArrivalTime()));
        departureColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getDepartureTime()));
        stopsTable.setItems(FXCollections.observableArrayList());

        priceTable.setEditable(true);
        priceCityColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getStop().getCity()));
        priceAdultColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getPriceFromStartAdult()));
        priceAdultColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        priceAdultColumn.setOnEditCommit(evt -> evt.getRowValue().setPriceFromStartAdult(evt.getNewValue()));
        priceChildColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getPriceFromStartChild()));
        priceChildColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        priceChildColumn.setOnEditCommit(evt -> evt.getRowValue().setPriceFromStartChild(evt.getNewValue()));
        priceTable.setItems(FXCollections.observableArrayList());
    }

    @Override
    protected void saveEntity() throws SQLException {
        if (!FormValidator.validateInput(stopsTable, driverComboBox, statusComboBox, busComboBox)) return;

        updateTripEntityFromForm();

        tripDao.save(entity);

        boolean hasTickets = ticketDao.hasTicketsForTrip(entity.getId());
        if (hasTickets) {
            AlertUtil.showError("Fehler", "Die Haltestellen und Preise können nicht geändert werden, da bereits Tickets verkauft wurden.");
            return;
        }

        seatAvailabilityDao.deleteAllForTrip(entity.getId());
        priceDao.deleteAllForTrip(entity.getId());
        stopDao.deleteAllForTrip(entity.getId());

        saveStops();
        savePrices();
        regenerateSeatAvailability();
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
        dlg.setTitle("Station hinzufügen");
        ButtonType addType = new ButtonType("Hinzufügen", ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(addType, ButtonType.CANCEL);

        ComboBox<City> cityCombo = new ComboBox<>();
        DatePicker arrDate = new DatePicker();
        buildSpinner(0, 23, 12);
        Spinner<Integer> arrHour = buildSpinner(0, 23, 12);
        Spinner<Integer> arrMinute = new Spinner<>(0, 59, 0, 1);

        DatePicker depDate = new DatePicker();
        Spinner<Integer> depHour = new Spinner<>(0, 23, 12);
        Spinner<Integer> depMinute = new Spinner<>(0, 59, 0, 1);

        try {
            cityCombo.getItems().setAll(cityDao.findAll());
        } catch (SQLException sqle) {
            AlertUtil.showError("Städte können nicht geladen werden", sqle.getMessage());
            return;
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(new Label("Stadt:"), 0, 0);
        grid.add(cityCombo, 1, 0);
        grid.add(new Label("Ankunftsdatum:"), 0, 1);
        grid.add(arrDate, 1, 1);
        grid.add(new Label("Stunde:"), 0, 2);
        grid.add(arrHour, 1, 2);
        grid.add(new Label("Minute:"), 0, 3);
        grid.add(arrMinute, 1, 3);
        grid.add(new Label("Abreisedatum:"), 0, 4);
        grid.add(depDate, 1, 4);
        grid.add(new Label("Stunde:"), 0, 5);
        grid.add(depHour, 1, 5);
        grid.add(new Label("Minute:"), 0, 6);
        grid.add(depMinute, 1, 6);

        dlg.getDialogPane().setContent(grid);

        Node addBtn = dlg.getDialogPane().lookupButton(addType);
        BooleanBinding valid = Bindings.createBooleanBinding(() ->
                        cityCombo.getValue() != null && arrDate.getValue() != null && depDate.getValue() != null,
                cityCombo.valueProperty(),
                arrDate.valueProperty(),
                depDate.valueProperty()
        );
        addBtn.disableProperty().bind(valid.not());

        dlg.setResultConverter(btn -> {
            if (btn == addType) {
                LocalDateTime arrival = LocalDateTime.of(arrDate.getValue(),
                        LocalTime.of(arrHour.getValue(), arrMinute.getValue())
                );
                LocalDateTime departure = LocalDateTime.of(depDate.getValue(),
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
        for (int i = 0; i < stops.size(); i++) {
            Stop s = stops.get(i);
            TripStopPrice p = existing.get(s.getId());

            if (p == null) {
                p = new TripStopPrice(entity, s, 0.0, 0.0);
            } else {
                p.setStop(s);
                if (i == 0) {
                    p.setPriceFromStartAdult(0.0);
                    p.setPriceFromStartChild(0.0);
                }
            }
            out.add(p);
        }
        priceTable.setItems(out);
    }

    private void updateTripEntityFromForm() {
        if (entity == null) {
            entity = new Trip(busComboBox.getValue(), driverComboBox.getValue(),
                    departureDatePicker.getValue(), statusComboBox.getValue());
        } else {
            entity.setBus(busComboBox.getValue());
            entity.setDriver(driverComboBox.getValue());
            entity.setDepartureDate(departureDatePicker.getValue());
            entity.setStatus(statusComboBox.getValue());
        }
    }

    private void saveStops() throws SQLException {
        int order = 1;
        for (Stop s : stopsTable.getItems()) {
            s.setTrip(entity);
            s.setStopOrder(order++);
        }
        stopDao.saveAllForTrip(entity.getId(), stopsTable.getItems());
    }

    private void savePrices() throws SQLException {
        priceDao.deleteAllForTrip(entity.getId());
        for (TripStopPrice p : priceTable.getItems()) {
            p.setTrip(entity);
        }
        priceDao.saveAllForTrip(entity.getId(), priceTable.getItems());
    }

    private void regenerateSeatAvailability() throws SQLException {
        SeatAvailabilityDao saDao = new SeatAvailabilityDao();
        saDao.deleteAllForTrip(entity.getId());

        List<Stop> stops = stopsTable.getItems();
        int seatCapacity = entity.getBus().getTotalSeats();
        int bicycleCapacity = entity.getBus().getBicycleSpaces();

        for (int i = 0; i < stops.size() - 1; i++) {
            for (int j = i + 1; j < stops.size(); j++) {
                saDao.save(new SeatAvailability(entity, stops.get(i), stops.get(j), seatCapacity, bicycleCapacity));
            }
        }
    }

    private Spinner<Integer> buildSpinner(int min, int max, int initial) {
        Spinner<Integer> spinner = new Spinner<>(min, max, initial);
        spinner.setEditable(true);
        return spinner;
    }

    private void renumberStops() {
        ObservableList<Stop> list = stopsTable.getItems();
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setStopOrder(i + 1);
        }
        stopsTable.refresh();
    }
}
