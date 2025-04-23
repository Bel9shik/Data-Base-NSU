package nsu.kardash.backendsportevents.services;

import ch.qos.logback.classic.layout.TTLLLayout;
import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.dto.responses.positive.FullEventResponse;
import nsu.kardash.backendsportevents.dto.responses.positive.ShortEventResponse;
import nsu.kardash.backendsportevents.exceptions.Event.EventNotFoundException;
import nsu.kardash.backendsportevents.models.Constants;
import nsu.kardash.backendsportevents.models.Event;
import nsu.kardash.backendsportevents.repositories.EventRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final TicketService ticketService;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(long id) {
        return eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException("Event not found"));
    }

    @Cacheable(value = "fullEventInfo", key = "#page + '-' + #size")
    public Page<FullEventResponse> getFullEventInfo (int page, int size) {
        Page<Event> eventPage = eventRepository.findAll(PageRequest.of(page, size, Sort.by("startedAt")));

        return eventPage.map( e -> {
            long busy = ticketService.countTicketsByEventAndStatusNot(e, Constants.cancelled);
            return new FullEventResponse(
                    e.getId(),
                    e.getName(),
                    e.getCost(),
                    e.getStartedAt(),
                    e.getEndedAt(),
                    e.getSeatCount(),
                    e.getSeatCount() - (int) busy
            );
        });
    }

    @Cacheable(value = "shortEventInfo", key = "#page + '-' + #size")
    public Page<ShortEventResponse> getShortEventInfo (int page, int size) {
        Page<Event> eventPage = eventRepository.findAll(PageRequest.of(page, size, Sort.by("startedAt")));

        return eventPage.map( e -> {
            long busy = ticketService.countTicketsByEventAndStatusNot(e, Constants.cancelled);
            return new ShortEventResponse(
                    e.getId(),
                    e.getName(),
                    e.getSeatCount() - (int) busy
            );
        });
    }

}
