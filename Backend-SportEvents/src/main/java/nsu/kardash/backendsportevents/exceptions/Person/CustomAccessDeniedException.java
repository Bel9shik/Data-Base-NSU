package nsu.kardash.backendsportevents.exceptions.Person;

public class CustomAccessDeniedException extends RuntimeException {
    public CustomAccessDeniedException(String message) {
        super(message);
    }
}
