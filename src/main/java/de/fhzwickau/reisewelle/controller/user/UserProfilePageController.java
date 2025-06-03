package de.fhzwickau.reisewelle.controller.user;

import de.fhzwickau.reisewelle.dao.TicketDao;
import de.fhzwickau.reisewelle.model.Authenticatable;
import de.fhzwickau.reisewelle.model.Ticket;
import de.fhzwickau.reisewelle.utils.AlertUtil;
import de.fhzwickau.reisewelle.utils.Session;
import de.fhzwickau.reisewelle.utils.WindowUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class UserProfilePageController {

    @FXML
    private Label emailLabel;

    @FXML
    private ListView<Ticket> ticketsListView;

    private final TicketDao ticketDao = new TicketDao();

    @FXML
    public void initialize() {
        try {
            Authenticatable currentUser = Session.getInstance().getCurrentUser();
            if (currentUser == null) {
                WindowUtil.openWindow("/de/fhzwickau/reisewelle/login-page.fxml", "Login", null);
                return;
            }

            emailLabel.setText(currentUser.getEmail());

            // Загрузка билетов пользователя
            List<Ticket> tickets = ticketDao.findByUserId(currentUser.getId());
            ticketsListView.setItems(FXCollections.observableList(tickets));

            // Кастомный cell factory для красивых карточек
            ticketsListView.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
                @Override
                protected void updateItem(Ticket ticket, boolean empty) {
                    super.updateItem(ticket, empty);
                    if (empty || ticket == null) {
                        setGraphic(null);
                    } else {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/fhzwickau/reisewelle/user/ticket_card.fxml"));
                            Pane card = loader.load();
                            TicketCardController ctrl = loader.getController();
                            ctrl.setTicket(ticket);
                            setGraphic(card);
                        } catch (IOException e) {
                            setText("Fehler beim Anzeigen des Tickets");
                        }
                    }
                }
            });

            // Открывать детали по двойному клику
            ticketsListView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    Ticket selectedTicket = ticketsListView.getSelectionModel().getSelectedItem();
                    if (selectedTicket != null) {
                        openTicketDetail(selectedTicket);
                    }
                }
            });
        } catch (SQLException e) {
            AlertUtil.showError("Fehler beim Laden der Tickets", e.getMessage());
        }
    }

    private void openTicketDetail(Ticket ticket) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/fhzwickau/reisewelle/user/detail_trip.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            DetailTripController controller = loader.getController();
            controller.setViewingTicket(true);

            controller.loadTripDetails(
                    ticket.getTrip().getId(),
                    ticket.getTotalPrice(),
                    ticket.getStartStop().getId().toString(),
                    ticket.getEndStop().getId().toString(),
                    ticket.getAdultCount(),
                    ticket.getChildCount(),
                    ticket.getBikeCount()
            );

            stage.setTitle("Ticket Details");
            stage.show();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            AlertUtil.showError("Fehler", ioe.getMessage());
        } catch (SQLException sqle) {
            AlertUtil.showError("Fehler", sqle.getMessage());
        }
    }
}
