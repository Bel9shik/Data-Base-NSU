package nsu.kardash.backendsportevents.exceptions.Ticket;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(String message) {
        super(message);
    }
}
