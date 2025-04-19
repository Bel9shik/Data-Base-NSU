package nsu.kardash.backendsportevents.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.dto.responses.positive.CabinetResponse;
import nsu.kardash.backendsportevents.services.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
@Schema(description = "Получение личных данных пользователя")
@SecurityRequirement(name = "JWT")
public class CabinetController {

    private final PersonService personService;

    @Operation(
            summary = "Данные о пользователе",
            description = "Позволяет получить более подробную информацию о пользователе"
    )
    @GetMapping("/info/{id}")
    public ResponseEntity<CabinetResponse> getCabinet(@PathVariable @Parameter(description = "идентификатор пользователя", required = true) long id) {

        return ResponseEntity
                .ok()
                .body(personService.getCabinet(id));
    }

}
