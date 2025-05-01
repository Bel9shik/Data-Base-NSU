package nsu.kardash.backendsportevents.services;

import nsu.kardash.backendsportevents.exceptions.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;

public class ValidationService {

    public static void checkValidationErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));

            throw new ValidationException(errors);
        }
    }
}
