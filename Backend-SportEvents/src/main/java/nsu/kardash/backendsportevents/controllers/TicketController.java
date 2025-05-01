package nsu.kardash.backendsportevents.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.dto.requests.ConfirmTicketDTO;
import nsu.kardash.backendsportevents.dto.requests.EventRequestDTO;
import nsu.kardash.backendsportevents.dto.responses.positive.*;
import nsu.kardash.backendsportevents.models.Constants;
import nsu.kardash.backendsportevents.models.Ticket;
import nsu.kardash.backendsportevents.services.AttributesService;
import nsu.kardash.backendsportevents.services.TicketService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/confirmList")
    @Operation(
            summary = "Admin access. Получение списка людей, у которых не подтверждён билет."
    )
    public ResponseEntity<List<ConfirmTicketResponse>> getAllEventsConfirmList(
            @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity
                .ok()
                .body(ticketService.confirmTickets(page, Constants.PAGE_SIZE).stream().toList());
    }

    @PostMapping("/registrationOnEvent")
    @Operation(
            summary = "Регистрация на мероприятие"
    )
    public ResponseEntity<RegistrationOnEventResponse> registerOnEvent(@RequestBody @Valid EventRequestDTO eventRequest, BindingResult bindingResult) {
        return ResponseEntity
                .ok()
                .body(ticketService.registerPersonOnEvent(eventRequest, bindingResult));
    }

    @PostMapping("/changeTicketStatus")
    @Operation(
            summary = "Admin access. Смена статуса билеты (Confirmed/Cancelled)"
    )
    public ResponseEntity<OkResponse> changeTicketStatus(@Valid @RequestBody ConfirmTicketDTO confirmTicketDTO) {
        return ResponseEntity
                .ok()
                .body(ticketService.changeTicketStatus(confirmTicketDTO));
    }


    @PostMapping("/deleteTicket")
    @Operation(
            summary = "Admin access. Удаление посетителя"
    )
    public ResponseEntity<OkResponse> deleteTicket (@RequestParam long ticketId) {

        return ResponseEntity
                .ok()
                .body(ticketService.deleteTicket(ticketId));
    }

    @GetMapping("/showFilteredTickets")
    @Operation(
            summary = "Admin access. Просмотр билетов с фильтрами"
    )
    public ResponseEntity<Page<TicketResponse>> searchEvents(
            @RequestParam Map<String, String> allParams,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "id,asc") String[] sort // e.g. ["surname","desc"]
    ) {

        return ResponseEntity
                .ok()
                .body(ticketService.findByFilters(allParams, page, Constants.PAGE_SIZE, sort));
    }

    @GetMapping("/attributes")
    @Operation(
            summary = "Admin access. Получение списка атрибутов для сортировки"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public ResponseEntity<AttributesResponse> getAttributes() {

        List<String> attributes = AttributesService.getAllAttributes(Ticket.class);
        attributes.remove("id");
        attributes.set(attributes.indexOf("event"), "eventID");
        attributes.set(attributes.indexOf("person"), "personID");

        return ResponseEntity
                .ok()
                .body(new AttributesResponse(attributes));
    }

    @GetMapping("/attendance")
    @Operation(
            summary = "Admin access. Составление отчёта по площадкам"
    )
    public ResponseEntity<Page<VenueAttendanceReport>> getVenueAttendanceReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) OffsetDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) OffsetDateTime endDate,
            @RequestParam(required = false) List<Long> venueIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        Page<VenueAttendanceReport> report = ticketService.generateRepost(startDate, endDate, venueIds, page, pageSize);
        return ResponseEntity.ok(report);
    }


}
