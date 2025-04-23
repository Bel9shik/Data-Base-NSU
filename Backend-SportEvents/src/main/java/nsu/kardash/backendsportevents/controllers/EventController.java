package nsu.kardash.backendsportevents.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.dto.responses.positive.FullEventResponse;
import nsu.kardash.backendsportevents.dto.responses.positive.ShortEventResponse;
import nsu.kardash.backendsportevents.services.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    @GetMapping("/fullEventInfo")
    @Operation(
            summary = "Полная информация по событиям (пагинация)"
    )
    public ResponseEntity<List<FullEventResponse>> getAllEventsFullInfo(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity
                .ok()
                .body(eventService.getFullEventInfo(page, size).stream().toList());
    }


    @GetMapping("/shortEventInfo")
    @Operation(
            summary = "Краткая информация по событиям (пагинация)"
    )
    public ResponseEntity<List<ShortEventResponse>> getAllEventsShortInfo(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity
                .ok()
                .body(eventService.getShortEventInfo(page, size).stream().toList());
    }

}
