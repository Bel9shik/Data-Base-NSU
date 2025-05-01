package nsu.kardash.backendsportevents.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.dto.requests.RefreshDTO;
import nsu.kardash.backendsportevents.dto.responses.positive.AuthenticationResponse;
import nsu.kardash.backendsportevents.services.RefreshService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Schema(description = "Обновление Refresh JWT токена")
public class RefreshController {

    private final RefreshService refreshService;

    @PostMapping("/refresh")
    @Operation(
            summary = "All access. Позволяет обновить JWT токен на новый токен, действующий неделю"
    )
    public ResponseEntity<AuthenticationResponse> refresh(@Valid @RequestBody RefreshDTO refreshDTO, BindingResult bindingResult) {

        return ResponseEntity
                .ok()
                .body(refreshService.refresh(refreshDTO, bindingResult));

    }
}
