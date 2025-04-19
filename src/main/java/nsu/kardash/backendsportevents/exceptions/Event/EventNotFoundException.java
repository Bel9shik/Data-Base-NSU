package nsu.kardash.backendsportevents.exceptions.Event;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String message) {
        super(message);
    }
}
