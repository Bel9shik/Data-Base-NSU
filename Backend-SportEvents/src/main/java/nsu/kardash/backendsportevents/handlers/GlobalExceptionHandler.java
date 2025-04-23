package nsu.kardash.backendsportevents.handlers;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import nsu.kardash.backendsportevents.dto.responses.errors.EventErrorResponse;
import nsu.kardash.backendsportevents.dto.responses.errors.PersonErrorResponse;
import nsu.kardash.backendsportevents.dto.responses.errors.ValidationErrorResponse;
import nsu.kardash.backendsportevents.exceptions.Event.EventNotFoundException;
import nsu.kardash.backendsportevents.exceptions.Person.*;
import nsu.kardash.backendsportevents.exceptions.Role.RoleNotFoundException;
import nsu.kardash.backendsportevents.exceptions.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PersonNotCreatedException.class)
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException ex) {
        return ResponseEntity
                .badRequest()
                .body(new PersonErrorResponse(ex.getErrors(), System.currentTimeMillis()));
    }

    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<PersonErrorResponse> handle(PersonNotFoundException e) {
        System.out.println("test exception");
        return ResponseEntity
                .badRequest()
                .body(new PersonErrorResponse(Map.of("error", e.getMessage()), System.currentTimeMillis()));
    }

    @ExceptionHandler(RefreshTokenNotFound.class)
    public ResponseEntity<PersonErrorResponse> handle(RefreshTokenNotFound e) {
        return ResponseEntity
                .badRequest()
                .body(new PersonErrorResponse(Map.of("error", e.getMessage()), System.currentTimeMillis()));
    }

    @ExceptionHandler(SignatureVerificationException.class)
    private ResponseEntity<PersonErrorResponse> handleException(SignatureVerificationException e) {
        return ResponseEntity
                .badRequest()
                .body(new PersonErrorResponse(Map.of("error", "Signature verification failed"), System.currentTimeMillis()));
    }

    @ExceptionHandler(ConfirmEmailException.class)
    private ResponseEntity<PersonErrorResponse> handleException(ConfirmEmailException e) {
        return ResponseEntity
                .badRequest()
                .body(new PersonErrorResponse(e.getErrors(), System.currentTimeMillis()));
    }

    @ExceptionHandler(SQLException.class)
    private ResponseEntity<PersonErrorResponse> handleException(SQLException e) {
        return ResponseEntity
                .badRequest()
                .body(new PersonErrorResponse(Map.of("error", e.getMessage().substring(e.getMessage().indexOf("Detail:") + 8)), System.currentTimeMillis()));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    private ResponseEntity<PersonErrorResponse> handleException(AuthorizationDeniedException e) {
        return ResponseEntity
                .badRequest()
                .body(new PersonErrorResponse(Map.of("error", e.getMessage()), System.currentTimeMillis()));
    }

    @ExceptionHandler(CustomAccessDeniedException.class)
    private ResponseEntity<PersonErrorResponse> handleException(CustomAccessDeniedException e) {
        return ResponseEntity
                .badRequest()
                .body(new PersonErrorResponse(Map.of("error", e.getMessage()), System.currentTimeMillis()));
    }

    @ExceptionHandler (ValidationException.class)
    private ResponseEntity<ValidationErrorResponse> handleException(ValidationException e) {
        return ResponseEntity
                .badRequest()
                .body(new ValidationErrorResponse(e.getErrors(), System.currentTimeMillis()));
    }

    @ExceptionHandler (EventNotFoundException.class)
    private ResponseEntity<EventErrorResponse> handleException(EventNotFoundException e) {
        return ResponseEntity
                .badRequest()
                .body(new EventErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler (RoleNotFoundException.class)
    private ResponseEntity<EventErrorResponse> handleException(RoleNotFoundException e) {
        return ResponseEntity
                .internalServerError()
                .build();
    }

}
