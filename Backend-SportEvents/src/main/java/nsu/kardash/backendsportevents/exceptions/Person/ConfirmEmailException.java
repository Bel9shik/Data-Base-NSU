package nsu.kardash.backendsportevents.exceptions.Person;

import lombok.Getter;

import java.util.Map;

@Getter
public class ConfirmEmailException extends RuntimeException {

    private Map<String, Object> errors;

    public ConfirmEmailException(Map<String, Object> errors) {
        this.errors = errors;
    }

}
