package nsu.kardash.backendsportevents.services;

import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.repositories.SportVenueRepository;
import nsu.kardash.backendsportevents.repositories.TicketRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SportVenuesService {

    private final SportVenueRepository sportVenueRepository;
    private final TicketRepository ticketRepository;


}
