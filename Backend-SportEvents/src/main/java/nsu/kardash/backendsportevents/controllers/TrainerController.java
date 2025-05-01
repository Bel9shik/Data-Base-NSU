package nsu.kardash.backendsportevents.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.dto.responses.positive.TrainerResponse;
import nsu.kardash.backendsportevents.services.TrainerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @GetMapping("/getAll")
    @Operation(
            summary = "Получение всех тренеров"
    )
    public ResponseEntity<List<TrainerResponse>> getTrainers() {

        return ResponseEntity
                .ok()
                .body(trainerService.findAll());

    }
}
