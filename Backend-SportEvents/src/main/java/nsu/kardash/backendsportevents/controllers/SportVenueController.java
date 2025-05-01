package nsu.kardash.backendsportevents.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.dto.requests.CreateSportVenueDTO;
import nsu.kardash.backendsportevents.dto.requests.UpdateSportVenueDTO;
import nsu.kardash.backendsportevents.dto.responses.positive.OkResponse;
import nsu.kardash.backendsportevents.dto.responses.positive.SportVenueResponse;
import nsu.kardash.backendsportevents.dto.responses.positive.VenueAttendanceReport;
import nsu.kardash.backendsportevents.services.SportVenueService;
import nsu.kardash.backendsportevents.services.ValidationService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sportVenues")
public class SportVenueController {

    private final SportVenueService sportVenueService;

    @PostMapping("/create")
    @Operation(summary = "Admin access. Создать новую спортивную площадку")
    public ResponseEntity<OkResponse> createVenue(@Valid @RequestBody CreateSportVenueDTO createSportVenueDTO, BindingResult bindingResult) {

        return ResponseEntity
                .ok()
                .body(sportVenueService.createSportVenue(createSportVenueDTO, bindingResult));
    }

    @GetMapping("/show")
    @Operation(summary = "Admin access. Посмотреть все спортивные площадки")
    public ResponseEntity<SportVenueResponse> createVenue() {

        return ResponseEntity
                .ok()
                .body(sportVenueService.showSportVenues());
    }

    @PostMapping("/update")
    @Operation(summary = "Admin access. Обновление спортивной площадки")
    public ResponseEntity<SportVenueResponse> updateSportVenue(@Valid @RequestBody UpdateSportVenueDTO updateSportVenueDTO, BindingResult bindingResult) {

        return ResponseEntity
                .ok()
                .body(sportVenueService.updateSportVenue(updateSportVenueDTO, bindingResult));
    }

    @PostMapping("/delete")
    @Operation(summary = "Admin access. Удаление спортивной площадки ")
    public ResponseEntity<OkResponse> updateSportVenue(@RequestParam @NotNull long sportVenueId) {

        return ResponseEntity
                .ok()
                .body(sportVenueService.deleteSportVenue(sportVenueId));
    }

}
