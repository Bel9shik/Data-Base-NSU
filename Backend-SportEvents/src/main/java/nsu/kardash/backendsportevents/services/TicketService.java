package nsu.kardash.backendsportevents.services;

import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.models.Event;
import nsu.kardash.backendsportevents.models.Ticket;
import nsu.kardash.backendsportevents.repositories.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public List<Ticket> getAllTickets(Event event) {
        return ticketRepository.findTicketsByEvent(event);
    }

    public long countTicketsByEventAndStatusNot(Event event, String status) {
        return ticketRepository.countByEventAndStatusNot(event, status);
    }

}
