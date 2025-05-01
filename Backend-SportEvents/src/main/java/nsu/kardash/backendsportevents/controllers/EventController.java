package nsu.kardash.backendsportevents.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.dto.requests.CreateEventDTO;
import nsu.kardash.backendsportevents.dto.responses.positive.EventShortResponse;
import nsu.kardash.backendsportevents.dto.requests.UpdateEventDTO;
import nsu.kardash.backendsportevents.dto.responses.positive.*;
import nsu.kardash.backendsportevents.models.Constants;
import nsu.kardash.backendsportevents.models.Event;
import nsu.kardash.backendsportevents.services.AttributesService;
import nsu.kardash.backendsportevents.services.EventService;
import nsu.kardash.backendsportevents.services.TicketService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;
    private final TicketService ticketService;

    @PostMapping("/createEvent")
    @Operation(
            summary = "Admin access. Создание события"
    )
    public ResponseEntity<OkResponse> createEvent(@RequestBody @Valid CreateEventDTO createEventDTO, BindingResult bindingResult) {

        return ResponseEntity
                .ok()
                .body(eventService.createEvent(createEventDTO, bindingResult));
    }

    @PostMapping("/updateEvent")
    @Operation(
            summary = "Admin access. Обновление события"
    )
    public ResponseEntity<OkResponse> updateEvent(@RequestBody @Valid UpdateEventDTO updateEventDTO, BindingResult bindingResult) {

        return ResponseEntity
                .ok()
                .body(eventService.updateEvent(updateEventDTO, bindingResult));
    }

    @PostMapping("/deleteEvent")
    @Operation(
            summary = "Admin access. Удаление события"
    )
    public ResponseEntity<OkResponse> deleteEvent (@RequestParam long eventId) {

        return ResponseEntity
                .ok()
                .body(eventService.deleteEvent(eventId));
    }

    @GetMapping("/showFilteredEventsFull")
    public ResponseEntity<Page<EventFullResponse>> searchEvents(
            @RequestParam Map<String, String> allParams,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "startedAt,asc") String[] sort // e.g. ["surname","desc"]
    ) {

        return ResponseEntity
                .ok()
                .body(eventService.fullEventInfoWithFilters(allParams, page, Constants.PAGE_SIZE, sort));
    }

    @GetMapping("/showFilteredEventsShort")
    public ResponseEntity<Page<EventShortResponse>> searchEvents(
            @RequestParam Map<String, String> allParams,
            @RequestParam(defaultValue = "startedAt,asc") String[] sort, // e.g. ["surname","desc"]
            @RequestParam(defaultValue = "0") int page
    ) {

        return ResponseEntity
                .ok()
                .body(eventService.shortEventInfoWithFilters(allParams, page, Constants.PAGE_SIZE, sort));
    }

    @GetMapping("/attributes")
    @Operation(
            summary = "Получение списка атрибутов для сортировки"
    )
    public ResponseEntity<AttributesResponse> getAttributes() {

        List<String> attributes = AttributesService.getAllAttributes(Event.class);
        attributes.remove("id");
        attributes.set(attributes.indexOf("venue"), "venueID");
        attributes.set(attributes.indexOf("trainer"), "trainerID");

        return ResponseEntity
                .ok()
                .body(new AttributesResponse(attributes));
    }
}
