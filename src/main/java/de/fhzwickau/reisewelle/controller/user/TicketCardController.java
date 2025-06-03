package de.fhzwickau.reisewelle.controller.user;

import de.fhzwickau.reisewelle.model.Ticket;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.format.DateTimeFormatter;

public class TicketCardController {

    @FXML
    private Label fromToLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label passengersLabel;

    public void setTicket(Ticket ticket) {
        if (ticket == null) return;
        String from = ticket.getStartStop() != null && ticket.getStartStop().getCity() != null
                ? ticket.getStartStop().getCity().getName() : "-";
        String to = ticket.getEndStop() != null && ticket.getEndStop().getCity() != null
                ? ticket.getEndStop().getCity().getName() : "-";
        fromToLabel.setText(from + " → " + to);

        priceLabel.setText(String.format("%.2f €", ticket.getTotalPrice()));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        dateLabel.setText("Gekauft: " + fmt.format(ticket.getPurchaseDate()));

        passengersLabel.setText(String.format("Erwachsene: %d, Kinder: %d, Fahrräder: %d",
                ticket.getAdultCount(), ticket.getChildCount(), ticket.getBikeCount()));
    }
}
