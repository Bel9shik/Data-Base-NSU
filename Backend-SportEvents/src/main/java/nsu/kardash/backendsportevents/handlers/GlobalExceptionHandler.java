package nsu.kardash.backendsportevents.handlers;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import nsu.kardash.backendsportevents.dto.responses.errors.*;
import nsu.kardash.backendsportevents.exceptions.Event.EventNotFoundException;
import nsu.kardash.backendsportevents.exceptions.Filters.FilterNotFoundException;
import nsu.kardash.backendsportevents.exceptions.Person.*;
import nsu.kardash.backendsportevents.exceptions.Role.RoleNotFoundException;
import nsu.kardash.backendsportevents.exceptions.SportVenue.SportVenueException;
import nsu.kardash.backendsportevents.exceptions.Ticket.TicketNotCreatedException;
import nsu.kardash.backendsportevents.exceptions.Ticket.TicketNotFoundException;
import nsu.kardash.backendsportevents.exceptions.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.sql.SQLException;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PersonNotCreatedException.class)
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException ex) {
        return ResponseEntity
                .badRequest()
                .body(new PersonErrorResponse(Map.of("error", ex.getMessage()), System.currentTimeMillis()));
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
                .badRequest()
                .body(new EventErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler (TicketNotFoundException.class)
    private ResponseEntity<EventErrorResponse> handleException(TicketNotFoundException e) {
        return ResponseEntity
                .internalServerError()
                .body(new EventErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler (TicketNotCreatedException.class)
    private ResponseEntity<EventErrorResponse> handleException(TicketNotCreatedException e) {
        return ResponseEntity
                .badRequest()
                .body(new EventErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler (SportVenueException.class)
    private ResponseEntity<EventErrorResponse> handleException(SportVenueException e) {
        return ResponseEntity
                .badRequest()
                .body(new EventErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler (FilterNotFoundException.class)
    private ResponseEntity<FilterErrorResponse> handleException(FilterNotFoundException e) {
        return ResponseEntity
                .badRequest()
                .body(new FilterErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler (HandlerMethodValidationException.class)
    private ResponseEntity<ValidationErrorResponse> handleException(HandlerMethodValidationException e) {
        return ResponseEntity
                .badRequest()
                .body(new ValidationErrorResponse(Map.of("error", e.getMessage().substring(e.getMessage().indexOf('\"'), e.getMessage().lastIndexOf('\"'))), System.currentTimeMillis()));
    }

    @ExceptionHandler (VerifyCodeException.class)
    private ResponseEntity<VerifyCodeErrorResponse> handleException(VerifyCodeException e) {
        return ResponseEntity
                .badRequest()
                .body(new VerifyCodeErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }
}
