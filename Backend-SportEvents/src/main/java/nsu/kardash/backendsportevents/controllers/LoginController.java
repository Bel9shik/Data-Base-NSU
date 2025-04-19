package nsu.kardash.backendsportevents.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.dto.requests.AuthenticationDTO;
import nsu.kardash.backendsportevents.dto.responses.positive.AuthenticationResponse;
import nsu.kardash.backendsportevents.services.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Schema(description = "Авторизация пользователя")
public class LoginController {

    private final LoginService loginService;

    //Проверка валидности данных и выдача нового JWT токена
    @PostMapping("/login")
    @Operation(
            summary = "Авторизация",
            description = "Авторизация пользователя, получение Access и Refresh JWT токенов"
    )
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationDTO authenticationDTO, BindingResult bindingResult) {

        return ResponseEntity
                .accepted()
                .body(loginService.login(authenticationDTO, bindingResult));

    }
}
