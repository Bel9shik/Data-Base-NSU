package nsu.kardash.backendsportevents.controllers;

import com.sun.jdi.request.EventRequest;
import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.dto.responses.positive.EventResponse;
import nsu.kardash.backendsportevents.models.Event;
import nsu.kardash.backendsportevents.services.EventService;
import nsu.kardash.backendsportevents.services.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final TicketService ticketService;

    public EventResponse createEvent(EventRequest eventRequest) {
        return null;
    }

    @GetMapping("/fullAll")
    public ResponseEntity<EventResponse> getAllEventsFullInfo() {

        List<Event> events = eventService.getAllEvents();

        Map<Integer, Map<String, String>> map = new HashMap<>();
        int index = 1;

        for (Event event : events) {
            map.put(index++, Map.of(
                    "cost", String.valueOf(event.getCost()),
                    "startedAt", String.valueOf(event.getStartedAt()),
                    "endedAt", String.valueOf(event.getEndedAt()),
                    "numSeatCount", String.valueOf(event.getSeatCount()),
                    "freeSeatCount", String.valueOf(event.getSeatCount() - ticketService.getAllTickets(event).size())
            ));
        }

        return ResponseEntity
                .ok()
                .body(new EventResponse(map));
    }

    @GetMapping("/shortAll")
    public ResponseEntity<EventResponse> getAllEventsShortInfo() {

        List<Event> events = eventService.getAllEvents();

        Map<Integer, Map<String, String>> map = new HashMap<>();
        int index = 1;

        for (Event event : events) {
            map.put(index++, Map.of(
                    "cost", String.valueOf(event.getCost()),
                    "freeSeatCount", String.valueOf(event.getSeatCount() - ticketService.getAllTickets(event).size())
            ));
        }

        return ResponseEntity
                .ok()
                .body(new EventResponse(map));
    }

}
