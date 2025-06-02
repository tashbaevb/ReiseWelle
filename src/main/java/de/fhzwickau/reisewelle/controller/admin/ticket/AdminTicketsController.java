package de.fhzwickau.reisewelle.controller.admin.ticket;

import de.fhzwickau.reisewelle.dao.TicketDao;
import de.fhzwickau.reisewelle.model.Ticket;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminTicketsController {

    @FXML
    private TableView<Ticket> ticketsTable;
    @FXML
    private TableColumn<Ticket, String> userColumn, tripColumn, startStopColumn, endStopColumn;
    @FXML
    private TableColumn<Ticket, Integer> adultsColumn, childrenColumn, bikesColumn;
    @FXML
    private TableColumn<Ticket, Double> priceColumn;
    @FXML
    private TableColumn<Ticket, String> purchaseDateColumn;

    private final TicketDao ticketDao = new TicketDao();

    @FXML
    public void initialize() {
        userColumn.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getUser() != null ? cell.getValue().getUser().getEmail() : "")
        );
        tripColumn.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getTrip() != null ? cell.getValue().getTrip().getId().toString() : "")
        );
        startStopColumn.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getStartStop() != null && cell.getValue().getStartStop().getCity() != null
                        ? cell.getValue().getStartStop().getCity().getName()
                        : "")
        );
        endStopColumn.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getEndStop() != null && cell.getValue().getEndStop().getCity() != null
                        ? cell.getValue().getEndStop().getCity().getName()
                        : "")
        );
        adultsColumn.setCellValueFactory(cell -> new SimpleIntegerProperty(
                cell.getValue().getAdultCount() != null ? cell.getValue().getAdultCount() : 0
        ).asObject());
        childrenColumn.setCellValueFactory(cell -> new SimpleIntegerProperty(
                cell.getValue().getChildCount() != null ? cell.getValue().getChildCount() : 0
        ).asObject());
        bikesColumn.setCellValueFactory(cell -> new SimpleIntegerProperty(
                cell.getValue().getBikeCount() != null ? cell.getValue().getBikeCount() : 0
        ).asObject());
        priceColumn.setCellValueFactory(cell -> new SimpleDoubleProperty(
                cell.getValue().getTotalPrice() != null ? cell.getValue().getTotalPrice() : 0.0
        ).asObject());
        purchaseDateColumn.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getPurchaseDate() != null
                        ? cell.getValue().getPurchaseDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                        : "")
        );

        loadTickets();
    }

    public void loadTickets() {
        try {
            List<Ticket> tickets = ticketDao.findAll();
            ticketsTable.setItems(FXCollections.observableList(tickets));
        } catch (Exception e) {
            AlertUtil.showError("Fehler beim Laden der Tickets", e.getMessage());
        }
    }

    @FXML
    private void onAdd() {
        openEditDialog(null);
    }

    @FXML
    private void onEdit() {
        Ticket selected = ticketsTable.getSelectionModel().getSelectedItem();
        if (selected != null) openEditDialog(selected);
        else AlertUtil.showInfo("Hinweis","Bitte wählen Sie ein Ticket.");
    }

    @FXML
    private void onDelete() {
        Ticket selected = ticketsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (AlertUtil.showConfirmation("Ticket löschen?", "Sind Sie sicher?")) {
                try {
                    ticketDao.delete(selected.getId());
                    loadTickets();
                } catch (Exception e) {
                    AlertUtil.showError("Fehler beim Löschen", e.getMessage());
                }
            }
        } else {
            AlertUtil.showInfo("Hinweis", "Bitte wählen Sie ein Ticket.");
        }
    }

    private void openEditDialog(Ticket ticket) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/fhzwickau/reisewelle/admin/ticket/add_edit_ticket.fxml"));
            Stage stage = new Stage();
            stage.setTitle(ticket == null ? "Neues Ticket" : "Ticket bearbeiten");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load()));

            AddEditTicketController ctrl = loader.getController();
            ctrl.setTicket(ticket);
            ctrl.setOnSaved(this::loadTickets);

            stage.showAndWait();
        } catch (Exception e) {
            AlertUtil.showError("Fehler", e.getMessage());
        }
    }
}
