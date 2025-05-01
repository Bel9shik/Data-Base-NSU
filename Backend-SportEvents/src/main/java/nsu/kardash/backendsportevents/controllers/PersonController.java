package nsu.kardash.backendsportevents.controllers;

import io.swagger.v3.oas.annotations.OpenAPI31;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.dto.requests.UpdatePersonDTO;
import nsu.kardash.backendsportevents.dto.responses.positive.AttributesResponse;
import nsu.kardash.backendsportevents.dto.responses.positive.OkResponse;
import nsu.kardash.backendsportevents.dto.responses.positive.PersonResponse;
import nsu.kardash.backendsportevents.models.Constants;
import nsu.kardash.backendsportevents.models.Person;
import nsu.kardash.backendsportevents.services.AttributesService;
import nsu.kardash.backendsportevents.services.PersonService;
import nsu.kardash.backendsportevents.services.RegistrationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/people")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final RegistrationService registrationService;

    @PostMapping("/updatePerson")
    @Operation(
            summary = "User access. Обновление данных пользователя"
    )
    public ResponseEntity<OkResponse> updateUser(@RequestBody @Valid UpdatePersonDTO updatePersonDTO, BindingResult bindingResult) {

        return ResponseEntity
                .ok()
                .body(personService.updatePerson(updatePersonDTO, bindingResult));
    }

    @PostMapping("/deletePerson")
    @Operation(
            summary = "User access. Удаление пользователя"
    )
    public ResponseEntity<OkResponse> deleteEvent () {

        return ResponseEntity
                .ok()
                .body(personService.deletePerson());
    }

    @PostMapping("/resetPassword")
    @Operation(
            summary = "All access. Восстановление пароля пользователя"
    )
    public ResponseEntity<?> resetPassword(@RequestParam @Email @NotNull String email) {
        return ResponseEntity
                .ok()
                .body(registrationService.resetPassword(email));
    }

    @GetMapping("/showFilteredUsers")
    @Operation(
            summary = "Admin access. Получение пользователей по фильтрам"
    )
    public ResponseEntity<Page<PersonResponse>> searchPersons(
            @RequestParam Map<String, String> allParams,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "id,asc") String[] sort // e.g. ["surname","desc"]
    ) {

        return ResponseEntity
                .ok()
                .body(personService.findByFilters(allParams, page, Constants.PAGE_SIZE, sort));
    }

    @GetMapping("/attributes")
    @Operation(
            summary = "Admin access. Получение списка атрибутов для сортировки"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public ResponseEntity<AttributesResponse> getAttributes() {

        List<String> attributes = AttributesService.getAllAttributes(Person.class);
        attributes.remove("id");
        attributes.remove("password");

        return ResponseEntity
                .ok()
                .body(new AttributesResponse(attributes));
    }

}
